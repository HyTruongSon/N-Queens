// Program: N-Queens problem solver (Genetic/Evolutionary Algorithm)
// Name: Hy Truong Son
// NEPTUN code: CM9MM4
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c) Hy Truong Son. All rights reserved.

import java.io.*;

import java.util.Scanner;
import java.util.Random;

public class N_queens {

	static int Population = 10000;
	static int Max_nGenerations = 100000000;
	static int nSwaps = 6;
	
	static String TextName = "Solution";
	static String ImageName = "Solution.png";
	
	static Random generator = new Random();
	public static int Random_Int(int n){
		return generator.nextInt(n);
	}
	
	static int N;
	
	static class aCitizen {
		public int N, nAttacks;
		public int Position_In_Row[];
		public int Col[];
		public int Diagonal_Sum[];
		public int Diagonal_Difference[];
		
		public void Reset(){
			nAttacks = 0;
			for (int i = 0; i < N; i++) Col[i] = 0;
			for (int i = 0; i < 2 * N; i++){
				Diagonal_Sum[i] = 0;
				Diagonal_Difference[i] = 0;
			}
		}
		
		public aCitizen(int New_N){
			N = New_N;
			Position_In_Row = new int [N];
			Col = new int [N];
			Diagonal_Sum = new int [2 * N];
			Diagonal_Difference = new int [2 * N];
		}
		
		public void ReCalculate_nAttacks(){
			Reset();
			for (int i = 0; i < N; i++){
				int j = Position_In_Row[i];
				nAttacks += Col[j] + Diagonal_Sum[i + j] + Diagonal_Difference[i - j + N];
				
				Col[j]++;
				Diagonal_Sum[i + j]++;
				Diagonal_Difference[i - j + N] ++;
			}
		}
		
		public void Assign(aCitizen another){
			this.N = another.N;
			for (int i = 0; i < N; i++)
				this.Position_In_Row[i] = another.Position_In_Row[i];
			ReCalculate_nAttacks();
		}
		
		public void Randomize(){
			for (int i = 0; i < N; i++)
				Position_In_Row[i] = Random_Int(N);
			ReCalculate_nAttacks();
		}
		
		public int Reduce(int row, int col1, int col2){
			int delta1 = (Col[col1] - 1) + (Diagonal_Sum[row + col1] - 1) + (Diagonal_Difference[row - col1 + N] - 1);
			int delta2 = Col[col2] + Diagonal_Sum[row + col2] + Diagonal_Difference[row - col2 + N];
			return delta1 - delta2;
		}
		
		public void Move(int row, int col1, int col2){
			Col[col1]--;
			Diagonal_Sum[row + col1]--;
			Diagonal_Difference[row - col1 + N]--;
			
			Col[col2]++;
			Diagonal_Sum[row + col2]++;
			Diagonal_Difference[row - col2 + N]++;
			
			Position_In_Row[row] = col2;
		}
		
		public void Local_Optimization(){
			boolean stop, moved;
			int next, Max_Reduce;
			
			while (true){
				stop = true;
				
				for (int i = 0; i < N; i++)	
					while (true){
						Max_Reduce = 0;
						next = 0;
						int j = Position_In_Row[i];
						
						if (j > 0)
							if (Max_Reduce < Reduce(i, j, j - 1)){
								Max_Reduce = Reduce(i, j, j - 1);
								next = -1;
							}
						
						if (j < N - 1)
							if (Max_Reduce < Reduce(i, j, j + 1)){
								Max_Reduce = Reduce(i, j, j + 1);
								next = 1;
							}
						
						if (Max_Reduce > 0){
							stop = false;
							nAttacks -= Max_Reduce;
							Move(i, j, j + next);
							continue;
						}
						
						break;
					}
					
				if (stop) break;
			}
		}
		
		public void Mutation(){
			for (int i = 0; i < nSwaps; i++){
				int u = Random_Int(N);
				int v = Random_Int(N);
				
				int temp = Position_In_Row[u];
				Position_In_Row[u] = Position_In_Row[v];
				Position_In_Row[v] = temp;
			}
		}
		
		public void Half_Father_Half_Mother_Crossover(aCitizen father, aCitizen mother){
			for (int i = 0; i < N / 2; i++)
				this.Position_In_Row[i] = father.Position_In_Row[i];
				
			for (int i = N / 2; i < N; i++)
				this.Position_In_Row[i] = mother.Position_In_Row[i];
			
			Mutation();
			ReCalculate_nAttacks();
			Local_Optimization();
		}
		
		public void Random_Crossover(aCitizen father, aCitizen mother){
			for (int i = 0; i < N; i++){
				int j = Random_Int(2);
				if (j == 0)
					this.Position_In_Row[i] = father.Position_In_Row[i];
				else
					this.Position_In_Row[j] = mother.Position_In_Row[i];
			}
			
			ReCalculate_nAttacks();
			Local_Optimization();
		}
	}
	
	static aCitizen Citizen[];
	static aCitizen result;
	static Heap Heap_Max, Heap_Min;
	static boolean found = false;
	
	//Buffer reader from system standard input
	static BufferedReader Buffer = new BufferedReader(new InputStreamReader(System.in));
	
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
		Citizen = new aCitizen [Population];
		
		for (int i = 0; i < Population; i++){
			Citizen[i] =  new aCitizen(N);
			Citizen[i].Randomize();
			Citizen[i].Local_Optimization();
		}
		
		Heap_Min = new Heap(Population, true);
		Heap_Max = new Heap(Population, false);
		
		for (int i = 0; i < Population; i++){
			Heap_Min.Push(Citizen[i].nAttacks, i);
			Heap_Max.Push(Citizen[i].nAttacks, i);
		}
	}
	
	public static void Update_Generation(aCitizen New_Citizen){
		int Max_Index = Heap_Max.Find_First_Best();
		
		if (New_Citizen.nAttacks < Citizen[Max_Index].nAttacks){
			Heap_Min.Pop(Heap_Min.pos[Max_Index]);
			Heap_Max.Pop(Heap_Max.pos[Max_Index]);
			
			Citizen[Max_Index].Assign(New_Citizen);
			
			Heap_Min.Push(Citizen[Max_Index].nAttacks, Max_Index);
			Heap_Max.Push(Citizen[Max_Index].nAttacks, Max_Index);
		}
	}
	
	public static void Update_Generation(int parent1, int parent2){
		aCitizen child = new aCitizen(N);
		
		child.Half_Father_Half_Mother_Crossover(Citizen[parent1], Citizen[parent2]);
		Update_Generation(child);
		
		child.Half_Father_Half_Mother_Crossover(Citizen[parent2], Citizen[parent1]);
		Update_Generation(child);
		
		child.Random_Crossover(Citizen[parent1], Citizen[parent2]);
		Update_Generation(child);
	}
	
	public static void Genetic_Algorithm(){
		int nGenerations = 0;
		result = new aCitizen(N);
		
		while (true){
			nGenerations++;
			System.out.println("\nGeneration " + nGenerations);
			
			int index1 = Heap_Min.Find_First_Best();
			
			System.out.println("Minimum of attacking: " + Citizen[index1].nAttacks);
			if (Citizen[index1].nAttacks == 0){
				found = true;
				result = Citizen[index1];
				break;
			}
			
			int index2 = Heap_Min.Find_Second_Best();
			Update_Generation(index1, index2);
			
			while (true){
				index1 = Random_Int(Population);
				index2 = Random_Int(Population);
				if (index1 != index2) break;
			}
			Update_Generation(index1, index2);
			
			index1 = Heap_Min.Find_First_Best();
			index2 = Heap_Max.Find_Second_Best();
			Update_Generation(index1, index2);
			
			if (nGenerations == Max_nGenerations) break;
		}
		
		result = Citizen[Heap_Min.Find_First_Best()];
	}
	
	public static void Write_Output(String FileName) throws IOException {
		FileWriter file = new FileWriter(FileName);
		PrintWriter writer = new PrintWriter(file);
		
		writer.println(N);
		for (int i = 0; i < N; i++){
			System.out.println("[" + i + ", " + result.Position_In_Row[i] + "]");
			writer.println(i + " " + result.Position_In_Row[i]);
		}
		
		writer.close();
		file.close();
	}
	
	public static void main(String args[]) throws IOException {
		Input();
		Init();
		Genetic_Algorithm();
		
		if (!found){
			System.out.println("\nThe evolutionary algorithm cannot find a solution."); 
			System.out.println("The most optimal solution is:");
		}else
			System.out.println("\nFound successfully a solution:");
			
		Write_Output(TextName);
		Visualization.Make_Image(ImageName, N, result.Position_In_Row);
	}	
	
}
