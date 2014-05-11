import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
public class CatAndMouseWorld implements RLWorld{
	public int bx, by;	// boundary peta

	public int mx, my;	// kordinat mouse
	public int mo;		// arah mouse, atas=0 kanan-atas=1 kanan=2 dst..
	
	public int[] cx, cy;	// kordinat2 kucing
	public int[] chx, chy;	// kordinat2 keju
	public int hx, hy;
	
	public int catscore = 0, mousescore = 0;		// skor awal tikus dan kucing
	public int cheeseReward=50, deathPenalty=100;	// nilai reward training

	
	public int view_limit = 10;						// batas penglihatan tikus
	public ArrayList<Integer> setPos;				// set posisi dari file eksternal
	public int nCat = 1;							// jumlah kucing
	public int nCheese = 1;							// jumlah keju
	public int curSetPos = 0;						// kursor di ArrayList setPos
	static final int NUM_OBJECTS=4, NUM_ACTIONS=3, WALL_TRIALS=100;	// banyak jenis halangan, banyak aksi
	static final double INIT_VALS=0;
		
	int[] stateArray;				// array untuk menyimpan state
	double waitingReward;			// reward yang dicatat setelah suatu aksi dilakukan
	public boolean[][] walls;		// matriks yang menyimpan posisi walls
	
	public int turn=0;				// variabel untuk greedy tikus

	public CatAndMouseWorld(int x, int y, int numWalls, int ncheese, int ncat, ArrayList<Integer> setPoss) {
		bx = x;
		by = y;
		cheeseReward=bx+by;
		deathPenalty=bx+by;
		Reader rd = new Reader();
		rd.ReadDataKoordinat();
		view_limit = rd.view_limit;
		nCat = ncat;
		nCheese = ncheese;
		chx =  new int[nCheese];
		chy = new int[nCheese];
		cx =  new int[nCat];
		cy = new int[nCat];
		setPos = setPoss;
		System.out.println("train : "+setPos.size());
		makeWalls(x,y,numWalls);
		resetState();
	}
	
	public CatAndMouseWorld(int x, int y, boolean[][] newwalls, int ncheese, int ncat, ArrayList<Integer> setPoss) {
		bx = x;
		by = y;
		cheeseReward=bx+by;
		deathPenalty=bx+by;	
		Reader rd = new Reader();
		rd.ReadDataKoordinat();
		view_limit = rd.view_limit;
		nCat = ncat;
		nCheese = ncheese;
		chx =  new int[nCheese];
		chy = new int[nCheese];
		cx =  new int[nCat];
		cy = new int[nCat];
		setPos = setPoss;
		System.out.println("play : "+setPos.size());
		walls = newwalls;
		resetState();
	}

	// getter setter
	public void setNCat(int ncat){
		nCat=ncat;
	}
	public int getNCat(){
		return nCat;
	}
	public void setNCheese(int ncheese){
		nCheese=ncheese;
	}
	public int getNCheese(){
		return nCheese;
	}
	
	/******* RLWorld interface functions ***********/
	public int[] getDimension() { 
		int[] retDim = new int[view_limit+1];
		int i;
		for (i=0; i<view_limit;) {
			retDim[i++] = NUM_OBJECTS;	// jenis halangan
			
		}
		retDim[i] = NUM_ACTIONS;		// jenis aksi
		
		return retDim;
	}
		
	// given action determine next state
	public int[] getNextState(int action) {
		// action is mouse action:  0=maju 1=putar kanan 2=putar kiri
		Dimension d = getCoords(action);
		int ax=d.width, ay=d.height;
		boolean hitWall = false;
		if (legal(ax,ay)) {
			// move agent
			mx = ax; my = ay;
		}
		else {
			hitWall = true;
			//System.out.println("nabrak");
		}
		// update world
		// moveCat();
		if (hitWall){
			mousescore -= 2;
			waitingReward = -2;
		}
		else{
			waitingReward = calcReward();
		}
		
		// if mouse has cheese, delete cheese
		for (int z =0 ;z<nCheese;z++){
			if ((mx==chx[z]) && (my==chy[z])) {
				chx[z] = -1;
				chy[z] = -1;
			}
		}
		
		return getState();
	}
	
	public double getReward(int i) { return getReward(); }
	public double getReward() {	return waitingReward; }
	
	public boolean validAction(int action) {
		Dimension d = getCoords(action);
		return legal(d.width, d.height);
	}
	
	Dimension getCoords(int action) {
		int ax=mx, ay=my;
		switch(action) {
			case 0:
				//System.out.println("maju");
				switch(mo) {
					case 0: ay = my - 1; break;
					case 1: ay = my - 1; ax = mx + 1; break;
					case 2: ax = mx + 1; break;
					case 3: ay = my + 1; ax = mx + 1; break;
					case 4: ay = my + 1; break;
					case 5: ay = my + 1; ax = mx - 1; break;
					case 6: ax = mx - 1; break;
					case 7: ay = my - 1; ax = mx - 1; break;
				}
			break;
			case 1:
				//System.out.println("putar kanan");
				mo++;
				if (mo>=8) mo=0;
			break;
			case 2:
				//System.out.println("putar kiri");
				mo--;
				if (mo<=-1) mo=7;
			break;
			default: //System.out.println("Invalid action: "+action);
		}
		return new Dimension(ax, ay);
	}

	public boolean endState() { return endGame(); }
	public int[] resetState() { 
		catscore = 0;
		mousescore = 0;
		mo = 0;
		setPos();
		return getState();
	}
		
	public double getInitValues() { return INIT_VALS; }
	/******* end RLWorld functions **********/
	
	public int[] getState() {
		// translates current state into int array
		stateArray = new int[view_limit];
		int obsX = mx;		// kordinat x obstacle yg akan dicek
		int obsY = my;		// kordinat y obstacle yg akan dicek
		int dX;				// perpindahan x obstacle
		int dY;				// perpindahan y obstace
		int obs = 4;
		// tentukan perpindahan berdasarkan arah mouse
		switch(mo){
			case 0 :	dX = 0;
						dY = -1;
						break;
			case 1 :	dX = 1;
						dY = -1;
						break;
			case 2 :	dX = 1;
						dY = 0;
						break;
			case 3 :	dX = 1;
						dY = 1;
						break;
			case 4 :	dX = 0;
						dY = 1;
						break;
			case 5 :	dX = -1;
						dY = 1;
						break;
			case 6 : 	dX = -1;
						dY = 0;
						break;
			case 7 :	dX = -1;
						dY = -1;
						break;
			default :	dX = 0;
						dY = 0;
						break;
		}
		obsX += dX;
		obsY += dY;
		for (int i =0;i<view_limit;i++){
			if (obsX<0 || obsX>=bx ||obsY<0 || obsY>=by){
				obs = 0;
			}
			else {
				if (walls[obsX][obsY])
					obs = 0;	// halangan = wall
				else
					obs = 3;	// halangan = kosong
					
				for (int z=0;z<nCat;z++){
					if(obsX==cx[z] && obsY==cy[z])
						obs = 1;	// halangan = kucing
				}
				for (int z=0;z<nCheese;z++){
					if (obsX==chx[z] && obsY==chy[z])
						obs = 2;	// halangan = keju
				}
			}
			stateArray[i] = obs;
			obsX += dX;
			obsY += dY;
		}
		return stateArray;
	}

	public double calcReward() {
		double newReward = 0;
		boolean kosong = true;
		for (int j=0; j<nCheese; j++){
			if ((mx==chx[j])&&(my==chy[j])) {
				mousescore += cheeseReward;
				newReward += cheeseReward;
				kosong = false;
			}
		}
		for (int j=0; j<nCat; j++){
			if ((cx[j]==mx) && (cy[j]==my)) {
				catscore++;
				mousescore -= deathPenalty;
				newReward -= deathPenalty;
				kosong = false;
			}
		}
		
		if (kosong){
			mousescore--;
			newReward--;
		}
		
		return newReward;		
	}
	
	public void setPos() {
		// Buat kopian matriks walls
		boolean[][] isi = new boolean[bx][by];
		for (int i=0; i<bx; i++) {
			for (int j=0; j<by; j++) {
				isi[i][j] = walls[i][j];
			}
		}
		
		// Tentuin posisi mouse
		int x, y;
		if (curSetPos >= setPos.size()) {
			curSetPos = 0;
		}
		x = setPos.get(curSetPos)-1;
		curSetPos++;
		y = setPos.get(curSetPos)-1;
		curSetPos++;
		if (curSetPos >= setPos.size()) {
			curSetPos = 0;
		}
		while (x>=bx || y>=by || isi[x][y]) {
			x = setPos.get(curSetPos)-1;
			curSetPos++;
			y = setPos.get(curSetPos)-1;
			curSetPos++;
			if (curSetPos >= setPos.size()) 
				curSetPos = 0;
		}
		mx = x;
		my = y;
		isi[mx][my] = true;
		
		// Tentuin posisi kucing
		for (int z = 0;z<nCat;z++){
			x = setPos.get(curSetPos)-1;
			curSetPos++;
			y = setPos.get(curSetPos)-1;
			curSetPos++;
			if (curSetPos >= setPos.size()) {
				curSetPos = 0;
			}
			while ( x>=bx || y>=by || isi[x][y]) {
				x = setPos.get(curSetPos)-1;
				curSetPos++;
				y = setPos.get(curSetPos)-1;
				curSetPos++;
				if (curSetPos >= setPos.size()) {
					curSetPos = 0;
				}
			}
			cx[z] = x;
			cy[z] = y;
			isi[cx[z]][cy[z]] = true;
		}
		
		// Tentuin posisi keju
		for (int z = 0;z<nCheese;z++){			
			x = setPos.get(curSetPos)-1;
			curSetPos++;
			y = setPos.get(curSetPos)-1;
			curSetPos++;
			if (curSetPos >= setPos.size()) 
				curSetPos = 0;
			while (x>=bx || y>=by || isi[x][y]) {
				x = setPos.get(curSetPos)-1;
				curSetPos++;
				y = setPos.get(curSetPos)-1;
				curSetPos++;
				if (curSetPos >= setPos.size()) {
					curSetPos = 0;
				}
			}
			chx[z] = x;
			chy[z] = y;
			isi[chx[z]][chy[z]] = true;
		}
	}

	boolean legal(int x, int y) {
		return ((x>=0) && (x<bx) && (y>=0) && (y<by)) && (!walls[x][y]);
	}

	boolean endGame() {
		// Cek tikus mati/ga
		boolean mouseDie = false;
		for (int i=0; i<nCat; i++) {
			if (mx == cx[i] && my == cy[i]) {
				return true;
			}
		}
		// Cek keju abis
		boolean cheeseGone = true;
		for (int i=0; i<nCheese; i++) {
			if (chx[i] != -1 || chy[i] != -1) {
				return false;
			}
		}
		return (mouseDie || cheeseGone);
	}

	Dimension getRandomPos() {
		int nx, ny;
		nx = (int)(Math.random() * bx);
		ny = (int)(Math.random() * by);
		for(int trials=0; (!legal(nx,ny)) && (trials < WALL_TRIALS); trials++){
			nx = (int)(Math.random() * bx);
			ny = (int)(Math.random() * by);
		}
		return new Dimension(nx, ny);
	}
	
	/******** heuristic functions ***********/
	
	int mouseAction() { // action greedy
		int act;
		int i;
		boolean adaKeju=false;
		int[] stateTemp = new int[view_limit];
		stateTemp = getState();
		for (i=0;i<view_limit;i++){
			if (stateTemp[i]==2){
				adaKeju=true;
				break;
			}
		}
		while(i>0 && i<view_limit){
			i--;
			if (stateTemp[i]!=3){
				i=-100;
				break;
			}		
		}
		if(adaKeju && i!=-100){
			act=0;
		}else{
			if (turn>=8){
				if (!(stateTemp[0]==0 || stateTemp[0]==1)){
					act = 0;
					turn=0;
				}
				else {
					act = (int)(Math.random() * 3);
					while (act==0 ){
						act = (int)(Math.random() * 3);
					}
				}
			}else{
				act=1; // muter ke kanan terus
				turn++;
			}
		}
		
		
		return act;
	}
	/******** end heuristic functions ***********/


	/******** wall generating functions **********/
	void makeWalls(int xdim, int ydim, int numWalls) {
		walls = new boolean[xdim][ydim];
		
		// loop until a valid wall set is found
		for(int t=0; t<WALL_TRIALS; t++) {
			// clear walls
			for (int i=0; i<walls.length; i++) {
				for (int j=0; j<walls[0].length; j++) walls[i][j] = false;
			}
			
			float xmid = xdim/(float)2;
			float ymid = ydim/(float)2;
			
			// randomly assign walls.  
			for (int i=0; i<numWalls; i++) {
				Dimension d = getRandomPos();
				
				// encourage walls to be in center
				double dx2 = Math.pow(xmid - d.width,2);
				double dy2 = Math.pow(ymid - d.height,2);
				double dropperc = Math.sqrt((dx2+dy2) / (xmid*xmid + ymid*ymid));
				if (Math.random() < dropperc) {
					// reject this wall
					i--;
					continue;
				}
				
				//System.out.println("w & h = " + d.width + " " + d.height);
				walls[d.width][d.height] = true;
			}
			
			// check no trapped points
			if (validWallSet(walls)) break;
			
		}
		
	}
	
	boolean validWallSet(boolean[][] w) {
		// copy array
		boolean[][] c;
		c = new boolean[w.length][w[0].length];
		
		for (int i=0; i<w.length; i++) {
			for (int j=0; j<w[0].length; j++) c[i][j] = w[i][j];
		}
		
		// fill all 8-connected neighbours of the first empty
		// square.
		boolean found = false;
		search: for (int i=0; i<c.length; i++) {
			for (int j=0; j<c[0].length; j++) {
				if (!c[i][j]) {
					// found empty square, fill neighbours
					fillNeighbours(c, i, j);
					found = true;
					break search;
				}
			}
		}
		
		if (!found) return false;
		
		// check if any empty squares remain
		for (int i=0; i<c.length; i++) {
			for (int j=0; j<c[0].length; j++) if (!c[i][j]) return false;
		}
		return true;
	}
	
	void fillNeighbours(boolean[][] c, int x, int y) {
		c[x][y] = true;
		for (int i=x-1; i<=x+1; i++) {
			for (int j=y-1; j<=y+1; j++)
				if ((i>=0) && (i<c.length) && (j>=0) && (j<c[0].length) && (!c[i][j])) 
					fillNeighbours(c,i,j);
		}
	}
	/******** wall generating functions **********/

}
