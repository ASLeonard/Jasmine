
public class BndVcfEntry extends VcfEntry {
	
	String[] altTokens;
	public BndVcfEntry(String line) throws Exception
	{
		super(line);
		altTokens =  getAlt().split("([)|(])");
	}
	
	/*
	 * Getting the end coordinate may require parsing the ALT field
	 */
	public long getEnd() throws Exception
	{
		if(hasInfoField("END"))
		{
			return Long.parseLong(getInfo("END"));
		}
		return Long.parseLong(altTokens[1].split(":")[1]);
	}
	
	/*
	 * Always call the type of a BND-style VCF translocation to ensure consistently with different formats
	 */
	public String getType()
	{
		return "TRA";
	}
	
	/*
	 * The graph ID here has to consider both chromosomes
	 */
	public String getGraphId() throws Exception
	{
		String id = getChromosome() + "_" + getChr2();
		if(Settings.USE_TYPE)
		{
			id += "_" + getType();
		}
		if(Settings.USE_STRAND)
		{
			id += "_" + getStrand();
		}
		return id;
	}
	
	/*
	 * The second chromosome can be found in either the CHR2 INFO field or the ALT field
	 */
	public String getChr2() throws Exception
	{
		if(hasInfoField("CHR2"))
		{
			return getInfo("CHR2");
		}
		return altTokens[1].split(":")[0];
	}
	
	/*
	 * The strands may need to be inferred from the ALT square bracket format
	 */
	public String getStrand() throws Exception
	{
		String res = getInfo("STRANDS");
		if(res.length() == 0)
		{
			return strandsFromAltFormat();
		}
		return "";
	}
	
	/*
	 * Determine the strands from the ALT square bracket format
	 */
	public String strandsFromAltFormat()
	{
		String alt = getAlt();
		if(alt.startsWith("["))
		{
			return "+-";
		}
		else if(alt.startsWith("]"))
		{
			return "--";
		}
		else if(alt.contains("["))
		{
			return "++";
		}
		else if(alt.contains("]"))
		{
			return "-+";
		}
		return "";
	}
	
	/*
	 * Since length is undefined, get the second coord instead
	 */
	public int getSecondCoord() throws Exception
	{
		return (int)getEnd();
	}
}
