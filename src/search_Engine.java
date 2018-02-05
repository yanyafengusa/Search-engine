
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Trie {// create a trie
	public char charValue; 
	public static final int intSize = 62;
	public boolean isName;
	public boolean boolWord; // whether is word
	public String primaryName;
	public int intCount;
	Trie[] chileNodes = new Trie[intSize];// child node

	public Trie(char cr) {
		giveValue(cr);
	}

	public void giveValue(char cr) {
		charValue = cr;
	}

	public char getValue() {// get current char
		return charValue;
	}

	public boolean isKeyWord() {
		return isName;
	}

	public void setisKeyWord(boolean boolValue) {
		isName = boolValue;
	}

	public boolean isWord() {// whether current path char can create a word
		return boolWord;
	}

	public void setIsWord(boolean boolValue) {// set it to a word
		boolWord = boolValue;
	}

	public void setPrimaryName(String strName) {
		primaryName = strName;
	}

	public String PrimaryName() {
		return primaryName;
	}

	public void AddCount() {
		intCount++;
	}

	public int CountNumber() {
		return intCount;
	}
}

class Primary_Name {
	public String primaryStringName;
	public int intTotalCount;
}

public class search_Engine {// main search engine page
	private void insertIntoTree(Trie toNRoot, String[] strWords, String primaryString) {// add word to trie
		for (int j = 0; j < strWords.length; j++) {
			String strWord = strWords[j];
			if (null == toNRoot || null == strWord || "".equals(strWord))
				return;

			char[] charArr = strWord.toCharArray();
			for (int i = 0; i < charArr.length; i++) {
				int intIndex = -1;
				if (charArr[i] >= 'A' && charArr[i] <= 'Z') {
					intIndex = charArr[i] - 'A' + 10;
				} else if (charArr[i] >= 'a' && charArr[i] <= 'z') {
					intIndex = charArr[i] - 'a' + 10 + 26;
				} else if (charArr[i] >= 'a' && charArr[i] <= 'z') {
					intIndex = charArr[i] - '0';
				} else {
					return;
				}

				Trie child_node = toNRoot.chileNodes[intIndex];// current child node
				if (null == child_node) {
					Trie tnNewNode = new Trie(charArr[i]);
					if (i == charArr.length - 1) {
						tnNewNode.setIsWord(true);
						if (j == strWords.length - 1) {
							tnNewNode.setisKeyWord(true);
							tnNewNode.setPrimaryName(primaryString);
						}
					}
					toNRoot.chileNodes[intIndex] = tnNewNode;
					toNRoot = tnNewNode;
				} else {
					if (i == charArr.length - 1 && j == strWords.length - 1) {
						child_node.setisKeyWord(true);
						child_node.setPrimaryName(primaryString);
					}
					toNRoot = child_node;
				}
			}
		}
	}

	private static TreeMap<String, Primary_Name> getStatisticResult(Trie toNRoot,// get the analysed result
			TreeMap<String, Primary_Name> hmPrime) {
		if (toNRoot == null) {
			return hmPrime;
		}

		if (toNRoot.isKeyWord()) {
			if (hmPrime.containsKey(toNRoot.PrimaryName())) {
				hmPrime.get(toNRoot.PrimaryName()).intTotalCount = hmPrime.get(toNRoot.PrimaryName()).intTotalCount
						+ toNRoot.CountNumber();
			} else {
				Primary_Name pnNew = new Primary_Name();
				pnNew.primaryStringName = toNRoot.PrimaryName();
				pnNew.intTotalCount = toNRoot.CountNumber();
				hmPrime.put(toNRoot.PrimaryName(), pnNew);
			}
		}

		for (Trie node : toNRoot.chileNodes) {
			if (node != null) {
				getStatisticResult(node, hmPrime);
			}
		}
		return hmPrime;
	}

	public static Trie getCompanyDic(String strPath) throws IOException {
		search_Engine ttCompany = new search_Engine();
		Trie toNRoot = new Trie(' ');

		File fileOut = new File(strPath);
		BufferedReader bReader = new BufferedReader(new FileReader(fileOut));

		String strLine;
		while ((strLine = bReader.readLine()) != null) {
			String primaryString = "";
			String[] strCpnNames = strLine.split("	");
			primaryString = strCpnNames[0];
			for (String strName : strCpnNames) {
				String[] strWords = strName.split(" ");
				strWords = filterIllegalChar(strWords);
				ttCompany.insertIntoTree(toNRoot, strWords, primaryString);
			}
		}
		bReader.close();
		return toNRoot;
	}

	public static void readArticleLine(Trie toNRoot) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please input article line: ");
		while (true) {
			String strLine = sc.nextLine();
			if (strLine == null || strLine == "")
				continue;
			if (isFinalLine(strLine))
				return;
		}
	}

	public static String[] parsingArticle(String strLine) {
		String regEx = "[。？！?.!]";
		Pattern p = Pattern.compile(regEx);
		String[] strSentences = p.split(strLine);
		for (int i = 0; i < strSentences.length; i++) {
			strSentences[i] = strSentences[i].trim();
		}
		for (String str : strSentences) {
			System.out.println(str);
		}
		return strSentences;
	}

	public static String[] parsingSentence(String strSentence) {
		String regEx = "\\s";
		Pattern p = Pattern.compile(regEx);
		String[] strWords = p.split(strSentence);
		for (int i = 0; i < strWords.length; i++) {
			strWords[i] = strWords[i].trim();
		}
		for (String strItems : strWords) {
			System.out.println(strItems);
		}
		return strWords;
	}

	public static Boolean isFinalLine(String strLine) {
		if (strLine == null || strLine.length() == 0)
			return false;

		String strReg = "[^.]";
		Pattern p = Pattern.compile(strReg);
		Matcher m = p.matcher(strLine);
		String strResult = m.replaceAll("").trim();
		if (strLine.equals(strResult)) {
			return true;
		} else {
			return false;
		}
	}

	public static String[] filterIllegalChar(String[] strArr) {
		if (strArr == null || strArr.length == 0)
			return null;

		for (int i = 0; i < strArr.length; i++) {
			String strReg = "[^a-zA-Z0-9]";
			Pattern p = Pattern.compile(strReg);
			Matcher m = p.matcher(strArr[i]);
			strArr[i] = m.replaceAll("").trim();
		}
		return strArr;
	}

	public static boolean hasChildNode(Trie tn) {
		for (int i = 0; i < tn.chileNodes.length; i++) {
			if (tn.chileNodes[i] != null) {
				return true;
			}
		}
		return false;
	}

	public static class GeneOriginDict {// generate the original dictionary
		public HashMap<String, Set<String>> origincalDct() throws IOException {
			HashMap<String, Set<String>> invertedIndexMap = new HashMap<String, Set<String>>();
			for (int n = 1; n <= 5; n++) {
				BufferedReader br = new BufferedReader(new FileReader("DocAfterAna//processedHTML_" + n + ".txt"));
				String line = "";
				StringBuffer buffer = new StringBuffer();
				StringBuffer bufferUrl = new StringBuffer();
				if ((line = br.readLine()) != null) {
					bufferUrl.append(line.trim());
				}
				for (int i = 0; i < 3; i++) {
					line = br.readLine();
				}
				while ((line = br.readLine()) != null) {
					buffer.append(line.trim() + " ");
				}
				String fileContent = buffer.toString();
				String UrlContent = bufferUrl.toString();

				String regEx = "\\s";
				Pattern p = Pattern.compile(regEx);

				String[] strWords = p.split(fileContent);

				for (int i = 0; i < strWords.length; i++) {
					strWords[i] = strWords[i].trim().toLowerCase();

					if (!invertedIndexMap.containsKey(strWords[i].toString())) {
						Set<String> tempSet = new HashSet<String>();
						tempSet.add(UrlContent);

						invertedIndexMap.put(strWords[i].toString(), tempSet);

					} else {
						Set<String> tempSet = new HashSet<String>();
						tempSet = invertedIndexMap.get(strWords[i].toString());
						tempSet.add(UrlContent);
						invertedIndexMap.put(strWords[i].toString(), tempSet);
					}
				}

				try {
					File file = new File("rawDic.txt");
					file.createNewFile();
					FileWriter fileWritter;
					if (n == 1) {
						fileWritter = new FileWriter(file.getName(), false);
					} else {
						fileWritter = new FileWriter(file.getName(), true);
					}

					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					for (String strItems : strWords) {

						String regEx_spe = "[`~!@#$%^&*()+=|{}':;',-//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"\']";
						Pattern pa = Pattern.compile(regEx_spe);
						Matcher m = pa.matcher(strItems);
						String dict_word = m.replaceAll("").trim().toLowerCase();
						if (dict_word != null && dict_word.length() != 0) {
							bufferWritter.append(dict_word);
							bufferWritter.newLine();
						}
					}
					bufferWritter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return invertedIndexMap;
		}
	}

	public static void main(String[] args) throws IOException {
		GeneOriginDict orginDict = new GeneOriginDict();
		HashMap<String, Set<String>> thisMap = orginDict.origincalDct();

		Trie toNRoot = getCompanyDic("rawDic.txt");
		TreeMap<String, Primary_Name> hmPrime = new TreeMap<String, Primary_Name>();
		TreeMap<String, Primary_Name> hmResult = getStatisticResult(toNRoot, hmPrime);

		File file = new File("Dic.txt");
		file.createNewFile();

		FileWriter fileWritter = new FileWriter(file.getName(), false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		try {
			for (Map.Entry<String, Primary_Name> entry : hmResult.entrySet()) {
				String key = entry.getKey().toString();
				bufferWritter.append(key);
				bufferWritter.newLine();
			}
			bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Type the word you are looking for: ");
		boolean end = false;
		while (!end) {
			String strLine = sc.nextLine().toLowerCase();
			StringBuffer UrlResult = new StringBuffer();
			if (strLine == null || strLine == "" || strLine.length() == 0) {
				continue;
			}
			if (isFinalLine(strLine)) {
				break;
			}

			String[] keyWords = strLine.split(" ");
			System.out.println("Your input is:");
			Set<String> list = new HashSet<String>();

			for (int i = 0; i < keyWords.length; i++) {
				if (thisMap.get(keyWords[i]) == null) {
					continue;
				} else {

					System.err.print(keyWords[i] + " ");
					if (i > 0) {
						list.retainAll(thisMap.get(keyWords[i]));
					} else {
						list = thisMap.get(keyWords[i]);
					}
				}

			}
			System.out.println();
			if (list.size() != 0) {
				for (String a : list) {
					UrlResult.append(a + "\n");
				}
				System.out.println("The word you are looking for found in the following:");
				System.out.println(UrlResult.toString());
			} else {
				System.out.println("This word you are looking for isn't in the dic.");
			}
			end = true;
		}
	}
}