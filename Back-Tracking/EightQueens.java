// Program: N-Queens problem solver (Back-Tracking Algorithm)
// Name: Hy Truong Son
// NEPTUN code: CM9MM4
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c) Hy Truong Son. All rights reserved.

import java.io.*;
import java.awt.*;
import javax.swing.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class EightQueens {
	
	static int Cell_Length = 50;
	static int Queen_Diameter = 30;
	
	static String TextName = "Solution";
	static String ImageName = "Solution.png";
	static String QueenImage1 = "Queen1.jpg";
	static String QueenImage2 = "Queen2.jpg";
	
	static int N;
	static boolean found;
	static int row[];
	static boolean col[];
	static boolean diagonal_sum[];
	static boolean diagonal_difference[];
	
	static int DX[] = {0, 0, 1, 1, 1};
	static int DY[] = {-1, 1, 0, -1, 1};
	
	static int Queen1[][] = new int [Queen_Diameter][Queen_Diameter];
	static int Queen2[][] = new int [Queen_Diameter][Queen_Diameter];
	
	//Buffer reader from system standard input
	static BufferedReader Buffer = new BufferedReader(new InputStreamReader(System.in));
	
	public static String GetType(String FileName){
		int i, j;
		String res;
		j = 0;
		for (i = 0; i < FileName.length(); i++)
			if (FileName.charAt(i) == '.'){
				j = i;
				break;
			}
		res = "";
		for (i = j + 1; i < FileName.length(); i++) res += FileName.charAt(i);
		return res;
	}
	
	public static void DrawRect(BufferedImage image, int x1, int y1, int x2, int y2, String color){
		Graphics2D g2d = image.createGraphics();
			
		if (color.equals("WHITE"))
			g2d.setColor(Color.WHITE);
			
		if (color.equals("GRAY"))
			g2d.setColor(Color.GRAY);
			
		if (color.equals("BLACK"))
			g2d.setColor(Color.BLACK);
		
		g2d.fillRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}
	
	public static int RGB(int red,int green,int blue){
		return (0xff000000) | (red << 16) | (green << 8) | blue;
	}
	
	public static void Draw_Queen(BufferedImage image, int x, int y){
		int color = ((x % 2) + (y % 2)) % 2;
		
		x = x * Cell_Length + Cell_Length / 2 - Queen_Diameter / 2;
		y = y * Cell_Length + Cell_Length / 2 - Queen_Diameter / 2;
		
		int GrayScale;
		for (int i = 0; i < Queen_Diameter; i++)
			for (int j = 0; j < Queen_Diameter; j++){
				if (color == 0) GrayScale = Queen1[i][j]; else
					GrayScale = 255 - Queen2[i][j];
				image.setRGB(x + i, y + j, RGB(GrayScale, GrayScale, GrayScale));
			}
	}
	
	public static void DrawTable(BufferedImage image){
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++){
				int color = ((i % 2) + (j % 2)) % 2;
				if (color == 0)
					DrawRect(image, i * Cell_Length, j * Cell_Length, (i + 1) * Cell_Length - 1, (j + 1) * Cell_Length - 1, "WHITE");
				else
					DrawRect(image, i * Cell_Length, j * Cell_Length, (i + 1) * Cell_Length - 1, (j + 1) * Cell_Length - 1, "BLACK");
			}
	}
	
	public static void Input() throws IOException {
	    try	{
	    	while (true){
	    		System.out.print("Size of the chessboard: ");
	    		N = Integer.parseInt(Buffer.readLine());
	    	
	    		if (N < 1)
	    			System.out.println("The table is invalid.\n");
	    		else
	    			break;
	    	}
	    }catch(IOException error){
	    	error.printStackTrace();
	    }
	}
	
	public static void Init(){
		row = new int [N];
		col = new boolean [N];
		diagonal_sum = new boolean [2 * N];
		diagonal_difference = new boolean [2 * N];
		
		for (int i = 0; i < N; i++)
			col[i] = false;
		
		for (int i = 0; i < 2 * N; i++){
			diagonal_sum[i] = false;
			diagonal_difference[i] = false;
		}
	}
	
	public static boolean Free_Cell(int i, int j){
		if ((col[j]) || (diagonal_sum[i + j]) || (diagonal_difference[i - j + N])) return false;
		return true;
	}
	
	public static int Counting_Free_Cells(int i, int j){
		int count = 0;
		for (int t = 0; t < 5; t++){
			int u = i + DX[t];
			int v = j + DY[t];
			
			while ((u >= 0) && (u < N) && (v >= 0) && (v < N)){
				if (Free_Cell(u, v)) count++;
				u += DX[t];
				v += DY[t];
			}
		}
		return count;
	}
	
	public static int Heuristics(int i, int order[], int nCells[]){
		int nChoices = 0;
		
		for (int j = 0; j < N; j++){
			if (!Free_Cell(i, j)) continue;
			
			order[nChoices] = j;
			nCells[nChoices] = Counting_Free_Cells(i, j);
			nChoices++;
		}
		
		for (int u = 0; u < nChoices; u++)
			for (int v = u + 1; v < nChoices; v++)
				if (nCells[u] < nCells[v]){
					int temp = order[u];
					order[u] = order[v];
					order[v] = temp;
					
					temp = nCells[u];
					nCells[u] = nCells[v];
					nCells[v] = temp;
				}
				
		return nChoices;
	}
	
	public static void BackTracking(int i){
		int order[] = new int [N];
		int nCells[] = new int [N];
		
		int nChoices = Heuristics(i, order, nCells);
		
		for (int v = 0; v < nChoices; v++){
			int j = order[v];
			row[i] = j;
			col[j] = true;
			diagonal_sum[i + j] = true;
			diagonal_difference[i - j + N] = true;
			
			if (i < N - 1) BackTracking(i + 1); else{
				found = true;
				break;
			}
			
			col[j] = false;
			diagonal_sum[i + j] = false;
			diagonal_difference[i - j + N] = false;
			
			if (found) break;
		}
	}
	
	public static boolean FindSolution(){
		found = false;
		BackTracking(0);
		
		if (!found){
			System.out.println("There is no solution.");
			return false;
		}
		
		System.out.println("A solution is: ");
		for (int i = 0; i < N; i++){
			System.out.print("[" + i + ", " + row[i] + "]");
			if (i < N - 1) System.out.print(" ");
		}
		System.out.println();
		
		return true;
	}
	
	public static void Write_Output(String FileName) throws IOException {
		FileWriter file = new FileWriter(FileName);
		PrintWriter writer = new PrintWriter(file);
		
		writer.println(N);
		for (int i = 0; i < N; i++) writer.println(i + " " + row[i]);
		
		writer.close();
		file.close();
	}
	
	public static void Get_Queen_Image(String FileName, int Queen[][]) throws IOException{
		File file = new File(FileName);
		BufferedImage image = ImageIO.read(file);
		
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		
		int GrayScale[][] = new int [width][height];
		
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++){
				int RGB = image.getRGB(i, j); 
				int R = (RGB & 0x00ff0000) >> 16;
				int G = (RGB & 0x0000ff00) >> 8;
				int B = RGB & 0x000000ff;
				GrayScale[i][j] = (R * 299 + G * 587 + B * 114) / 1000;
			}
			
		Normalization.Resize(GrayScale, Queen, width, height, Queen_Diameter, Queen_Diameter);
	}
	
	public static void Make_Image(String FileName) throws IOException {
		BufferedImage image = new BufferedImage(Cell_Length * N, Cell_Length * N, BufferedImage.TYPE_INT_RGB);
		DrawTable(image);
		
		Get_Queen_Image(QueenImage1, Queen1);
		Get_Queen_Image(QueenImage2, Queen2);
		
		for (int i = 0; i < N; i++)
			Draw_Queen(image, i, row[i]);
		
		File file = new File(FileName);
		ImageIO.write(image, GetType(FileName), file);
	}
	
	public static void main(String args[]) throws IOException {
		Input();
		Init();
		if (FindSolution()){
			Write_Output(TextName);
			Make_Image(ImageName);
		}
	}	
	
}
