package textfiles;
import java.io.*;
import java.FileReader;
import java.io.BufferedReader;
import java.util.*;

public Class Text {
	public static void main(String[] args){
		Scanner S = new Scanner(new File("tabelpelatihan.txt"));
		int lihat, keju, kucing;
		try{
			String line = s.nextLine(); //line pertama
			lihat = Integer.parseInt(line);

			line = s.nextLine(); //line  kedua
			String[] temp = s.split(" ");
			keju = Integer.parseInt(temp[0]);
			kucing = Integer.parseInt(temp[1]);

			line = s.nextLine(); //line ketiga
			String[] temp1 = s.split(" ");
			String[] point = new String[temp1.length];
			for (int i=0; i<temp1.length; i++){
				String[] temp2 = temp1.substring(1, temp1.length-1).split(",");
				point[i] = temp2[i];
			}

			line = s.nextLine(); //line keempat
			String[] temp3 = s.split(" ");
			String[] point1 = new String[temp3.length];
			for (int j=0; j<temp1.length; j++){
				String[] temp4 = temp3.substring(1, temp3.length-1).split(",");
				point1[j] = temp4[j];
			}



		}
		file.close;		
		}
		catch (FileNotFoundException e){}
}

// if (line.charAt(i) == '<' || line.charAt(i) == '>')

(2 1)