package textfiles;
import java.io.*;
import java.FileReader;
import java.io.BufferedReader;
import java.util.*;

public Class ReadPetaPelatihan {
	public static void main(String[] args){
		Scanner S = new Scanner(new File("petapelatihan.txt"));
		int posisi;
		try{
			while (S.hasNext){
				String line = s.nextLine();
				String[] temp = s.split(" ");
				for (int i=0; i<temp.length; i++){
					posisi = Integer.parseInt(temp[i]);
				}	
			}	
		}
	}
}