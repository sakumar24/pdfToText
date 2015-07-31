package converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.Tesseract;

class Converter
{
	private static Pattern marksPat = Pattern.compile("(\\(\\d\\d?.*\\)$)");
	private static Pattern sectionPat = Pattern.compile("(^SECTION\\s*-\\s*\\w$)");
	private static Pattern pageNoPat = Pattern.compile("(^\\d\\s+\\d+$)");
	private static Pattern questionPat = Pattern.compile("(^(OR)?\\s*\\d?\\.?\\s*(\\(\\w\\))?)");
	private static String folderPath;
	
	public void convert(String path)
	{
		folderPath = path;
		
		int fileCount=0;
		if(folderPath == null)
			System.exit(0);
		File dir = new File(folderPath);
		File[] files = dir.listFiles();

		for(File file : files)
		{
			try
			{
				if(file.isFile())
				{
					
					String fileData = readAndFormat(file);
					writeData(fileData,file.getName());
					fileCount++;
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}			
		ConverterGUI.appendLogs("\n** Total number of files converted :"+fileCount+"\n");
	}

	private static void writeData(String data, String fileName)
	{
		fileName = fileName.replace(".pdf", ".txt");
		File dir = new File(folderPath+"\\Formated_Text");
		if(dir.exists())
			dir.delete();
		dir.mkdir();

		String outFile = dir.getAbsolutePath()+"\\"+fileName;
		ConverterGUI.appendLogs("** Writing File :: "+outFile+"\n");
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			String[] lines = data.split("\n");
			for(String line : lines)
			{
				line = line.trim();
				if(line.contains(":"))
				{
					String[] temp = line.split(":");
					for(String str : temp)
					{
						bw.write(str);
						bw.write(":");
						bw.newLine();
					}
				}
				else
				{
					bw.write(line);
					bw.newLine();
				}
			}
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static String readAndFormat(File file) throws Exception
	{
		ConverterGUI.appendLogs("\n** Converting File :: "+file.getName()+"\n");
		Tesseract tess = new Tesseract(); 
		
		String fileName = file.getName().split(".pdf")[0];
		String[] fileNameArr = fileName.split(" ");
		int fileNamelen = fileNameArr.length;
		String month = fileNameArr[fileNamelen-2]+" "+fileNameArr[fileNamelen-1];

		StringBuilder fileData = new StringBuilder();
		String res = tess.doOCR(file);

		int afterSecA = 0;
		String[] lines = res.split("\n");
		for(String line : lines)
		{
			Matcher sectionMatcher = sectionPat.matcher(line);
			Matcher pageNoMatcher = pageNoPat.matcher(line);
			Matcher questionMatcher = questionPat.matcher(line); 

			line = questionMatcher.replaceAll("");

			if(line.startsWith("OR"))
				line.replace("OR","");
			line = line.trim();

			if(sectionMatcher.find())
				afterSecA = 1;
			else if(afterSecA!=0 && ! pageNoMatcher.find())
			{
				String appendStr = " ";
				Matcher marksMatcher = marksPat.matcher(line);
				if(marksMatcher.find())
				{
					String marks = marksMatcher.group(0);
					line = marksMatcher.replaceAll("");
					appendStr = line+"\n"+month+" "+marks+"\n";
				}
				else
				{
					if(line.contains("P.T.O"))
						appendStr = "\n";
					else
						appendStr = line+" ";
				}
				//	System.out.println("App:"+appendStr);
				fileData.append(appendStr);
			}
		}
		return fileData.toString();
	}
}