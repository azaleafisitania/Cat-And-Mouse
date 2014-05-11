import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author @@@
 */
public class Reader {
	

    /**
     * @param args the command line arguments
     */
	 
	     
	boolean[][] fileMap;
	public ArrayList<Integer> set_posisi_play;
	public ArrayList<Integer> set_posisi_train;
	
	
	public int view_limit;
	public int nCat;
	public int nCheese;
	
	//membaca koordinat dari file external
    public void ReadDataKoordinat() {
        
        try 
        {
	        //buka file     
	        Scanner fileIn = new Scanner(new File("Tabel pelatihan.txt"));
	        String temp;
	        
	        //regexSplitter
	        String delims = "[(\\,\\)]+";
	        
	        set_posisi_play = new ArrayList<>();
	        set_posisi_train = new ArrayList<>();
	        
	        view_limit = Integer.parseInt(fileIn.next());
			
	        nCheese = Integer.parseInt(fileIn.next());
	        nCat = Integer.parseInt(fileIn.next());
			
	        temp = fileIn.nextLine();
			
	        while(fileIn.hasNext()) 
	        {
				temp = fileIn.nextLine();
		        String[] temp2 = temp.split(delims);
				for (int i = 1; i < temp2.length; i+=3) 
				{
					set_posisi_play.add(Integer.parseInt(temp2[i]));
					set_posisi_play.add(Integer.parseInt(temp2[i+1]));
				}
				
				temp = fileIn.nextLine();
				temp2 = temp.split(delims);
				
				for (int i = 1; i < temp2.length; i+=3) 
				{
					set_posisi_train.add(Integer.parseInt(temp2[i])); 
					set_posisi_train.add(Integer.parseInt(temp2[i+1])); 
				}
			}
			
        }
        
         catch (FileNotFoundException ex) 
         {
        	 
         } 
    }
        
        

    
    public void ReadDataMap() {    	
        try {   
        Scanner openFile;
        openFile = new Scanner(new File("Peta pelatihan.txt"));
        
        //mapnya masih satu dimensi, tar kalo mau dijadiin 2 dimensi gampang sih
        ArrayList<Integer> map = new ArrayList<>();
		int i =0;
		int j =0;
		String temp;
		String splitRegex = "[ ]+";
		String[] temp2;

		ArrayList<Boolean> tempAwal = new ArrayList<Boolean>();;
		ArrayList<ArrayList<Boolean>> tempMap = new ArrayList<ArrayList<Boolean>>();



        while(openFile.hasNext()) 
        {
	        
	        temp = openFile.nextLine();
			temp2 = temp.split(splitRegex);
			tempAwal = new ArrayList<Boolean>();
			
			
			for (i = 0;i<temp2.length;i++)
			{
				if (temp2[i].equals("1"))
				{
					tempAwal.add(true);
				}
				else
				{
					tempAwal.add(false);
				}
			}
			tempMap.add(tempAwal);
	
        }
        
		fileMap = new boolean[tempMap.size()][tempAwal.size()];
		for (i = 0 ; i<tempMap.size();i++)
		{
			for(j=0;j<tempAwal.size();j++)
			{
				fileMap[i][j]=tempMap.get(i).get(j);
			}
		}	
		
        
        } 
        catch (FileNotFoundException ex) 
        {
          //  Logger.getLogger(CatMouse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        }

	

	public boolean[][] getFileMap(){
		return fileMap;
	}

}


