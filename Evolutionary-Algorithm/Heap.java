import java.lang.*;
import java.io.*;

public class Heap {
	
	public int nHeap;
	public boolean Property; //True - Min Heap, False - Max Heap	
	public int value[];
	public int index[];
	public int pos[];
	
	public Heap(int Maximum_Size, boolean New_Property){
		nHeap = 0;
		value = new int [Maximum_Size + 1];
		index = new int [Maximum_Size + 1];
		pos = new int [Maximum_Size + 1];
		Property = New_Property;	
	}
	
	public void Swap(int u, int v){
		int temp = value[u];
		value[u] = value[v];
		value[v] = temp;
		
		temp = index[u];
		index[u] = index[v];
		index[v] = temp;
		
		pos[index[u]] = u;
		pos[index[v]] = v;
	}
	
	public void Up(int node){
		int parent;
		while (node > 1){
			parent = node / 2;
			if (value[node] < value[parent]){
				Swap(node, parent);
				node = parent;
			}else break;
		}
	}
	
	public void Down(int node){
		int child;
		while (2 * node <= nHeap){
			child = 2 * node;
			if ((child < nHeap) && (value[child + 1] < value[child])) child++;
			if (value[child] < value[node]){
				Swap(node, child);
				node = child;
			}else break;
		}
	}
	
	public void Push(int New_Value, int New_Index){
		nHeap++;
		if (Property) 
			value[nHeap] = New_Value;
		else
			value[nHeap] = - New_Value;
		index[nHeap] = New_Index;
		pos[New_Index] = nHeap;
		Up(nHeap);
	}
	
	public void Pop(int node){
		value[node] = value[nHeap];
		index[node] = index[nHeap];
		pos[index[node]] = node;
		nHeap--;
		Up(node);
		Down(node);
	}
	
	public int Find_First_Best(){
		return index[1];	
	}
	
	public int Find_Second_Best(){
		if (nHeap == 2) return index[2];
		if (value[2] < value[3]) return index[2];
		return index[3];
	}
	
}
