import java.util.Scanner;
import java.util.*;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
class optimal
{
	public static void main(String[] args) 
	{
        if(args.length==8) {
			int BLOCKSIZE = Integer.parseInt(args[0]);
			int L1_SIZE = Integer.parseInt(args[1]);
			int L1_ASSOC = Integer.parseInt(args[2]);
			int L2_SIZE = Integer.parseInt(args[3]);
			int L2_ASSOC = Integer.parseInt(args[4]);
			int REPLACEMENT_POLICY = Integer.parseInt(args[5]);
			int INCLUSION_PROPERTY = Integer.parseInt(args[6]);
			String test_file = args[7];
			
			int sets = (int)(L1_SIZE)/(L1_ASSOC*BLOCKSIZE);
			int count=0;
			
			int index_bits = (int)(Math.log(sets)/Math.log(2));
			int offset_bits = (int)(Math.log(BLOCKSIZE)/Math.log(2));
			int tag_bits = 32-index_bits-offset_bits;
			
			
			int L1_reads = 0;
			int L1_read_misses = 0;
			int L1_writes = 0;
			int L1_write_misses = 0;
			float L1_miss_rate = 0;
			int L1_writebacks = 0;
			
			
			ArrayList<String> optimal = new ArrayList<>();
			ArrayList<String> optimal_tag_index = new ArrayList<>();
			try {
				File myObj = new File(test_file);
				Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) {
					String data = myReader.nextLine();
					String[] data_string = data.split(" ", 2);
					int decimal = Integer.parseInt(data_string[1], 16);
					String operation = data_string[0];
					String binary = Integer.toBinaryString(decimal);
				
					if(binary.length() < 32){
						int diff = 32 - binary.length();
						String pad = "";
						for(int i = 0; i < diff; i++){
							pad = pad.concat("0");
						}
						binary = pad.concat(binary);
					}
					//System.out.println("k = " + k);
					String index = binary.substring(32-index_bits-offset_bits, 32-offset_bits);
					String offset = binary.substring(32-offset_bits, 32);
					String tag = binary.substring(0,32-index_bits-offset_bits);
					int decimal_tag = Integer.parseInt(tag,2);
					String hex_tag = Integer.toString(decimal_tag,16);
					//System.out.println(hex_tag);
					optimal.add(hex_tag);
					optimal_tag_index.add(tag+index);
			}
			//System.out.println(optimal);
			//System.out.println(optimal.size());
			myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
			e.printStackTrace();
			}
			
			String[][] tag_and_index = new String[sets][L1_ASSOC];
			String[][] L1_CACHE = new String[sets][L1_ASSOC];
			int[][] DIRTY_BIT = new int[sets][L1_ASSOC];
			int[][] VALID_BIT = new int[sets][L1_ASSOC];


			try {
				File myObj = new File(test_file);
				Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) {
					count++;
					String data = myReader.nextLine();
					String[] data_string = data.split(" ", 2);
					int decimal = Integer.parseInt(data_string[1], 16);
					String operation = data_string[0];
					String binary = Integer.toBinaryString(decimal);
					if(binary.length() < 32){
						int diff = 32 - binary.length();
						String pad = "";
						for(int i = 0; i < diff; i++){
							pad = pad.concat("0");
						}
						binary = pad.concat(binary);
					}
				
					String index = binary.substring(32-index_bits-offset_bits, 32-offset_bits);
					String offset = binary.substring(32-offset_bits, 32);
					String tag = binary.substring(0,32-index_bits-offset_bits);

					String tag_index = tag + index;
				
					int decimal_tag = Integer.parseInt(tag,2);
					String hex_tag = Integer.toString(decimal_tag,16);
					int decimal_index = Integer.parseInt(index,2);
					boolean status = false;
					//System.out.println(tag + "  " + index + "  " +  decimal_index + "  " + offset);
					//System.out.println(hex_tag + "  " + index + "  " +  decimal_index + "  " + offset);
					if(operation.charAt(operation.length()-1) == 'w') {
						status = false;
						L1_writes = L1_writes + 1;
						for (int i=0; i < L1_ASSOC ; i++) {
							if (VALID_BIT[decimal_index][i] == 1){
								if (L1_CACHE[decimal_index][i].equals(hex_tag)) {
									DIRTY_BIT[decimal_index][i] = 1;
									status = true;
									break;
								}
							}
						}
						if (status == false) {
							L1_write_misses = L1_write_misses + 1;
						}
					} 

					if(operation.charAt(operation.length()-1) == 'r') {
						status = false;
						L1_reads = L1_reads + 1;
						for (int i=0; i < L1_ASSOC ; i++) {
							if (VALID_BIT[decimal_index][i] == 1){
								if (L1_CACHE[decimal_index][i].equals(hex_tag)) {
									status = true;
									//System.out.println("status = " + status);
									break;
								}
							}
						}
						if (status == false) {
							//System.out.println("status = " + status);
							L1_read_misses = L1_read_misses + 1;
						}
					}
					if(status ==  false) {
						boolean status1 = false;
						for (int i = 0; i < L1_ASSOC ; i++) {
							if (VALID_BIT[decimal_index][i] == 0) {
								L1_CACHE[decimal_index][i] = hex_tag;
								VALID_BIT[decimal_index][i] = 1;
								tag_and_index[decimal_index][i] = tag_index;
								status1 = true;
								if (operation.charAt(operation.length()-1) == 'w') {
									DIRTY_BIT[decimal_index][i] = 1;
								}
								else {
									DIRTY_BIT[decimal_index][i] = 0;
								}
								break;
							}
						}
						if (status1 == false) {
							int replacement_index = 0;
							//System.out.println(tag + "  " + index + "  " +  decimal_index + "  " + offset);
							int[] temp = new int[L1_ASSOC];
							boolean did_not_find = false;
							int c = 0;
							for (int j=0; j<L1_ASSOC; j++){
								for (int i=count-1; i<optimal.size(); i++) {
									if (tag_and_index[decimal_index][j].equals(optimal_tag_index.get(i))) {
										//System.out.println(" i = " + optimal.get(i) + " count = " + count);
										//System.out.println(L1_CACHE[decimal_index][0] + "  " + L1_CACHE[decimal_index][1]);
										temp[j] = i;
										c = c + 1;
										break;
										}
									}
								if (c==j) {
									replacement_index = j;
									did_not_find = true;
									break;
								}
							}
							if(did_not_find == false) {
								int max_idx = 0;
								for (int k=1; k<L1_ASSOC; k++) {
									if (temp[k] > temp[max_idx]) {
										max_idx = k;
									}
								}
								replacement_index = max_idx;
							}
							//System.out.println("replaceme" + replacement_index );
							if (DIRTY_BIT[decimal_index][replacement_index] == 1){
								L1_writebacks = L1_writebacks + 1;
							}
							L1_CACHE[decimal_index][replacement_index] = hex_tag;
							VALID_BIT[decimal_index][replacement_index] = 1;
							tag_and_index[decimal_index][replacement_index] = tag_index;
							if (operation.charAt(operation.length()-1) == 'w') {
								DIRTY_BIT[decimal_index][replacement_index] = 1;
							}else {
								DIRTY_BIT[decimal_index][replacement_index] = 0;
							}
						}
					}
					
				} // end while
			myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			} // end catch
			
			
			System.out.println("===== Simulator configuration =====");
			System.out.println("BLOCK SIZE          : " + BLOCKSIZE);
			System.out.println("L1 SIZE             : " + L1_SIZE);
			System.out.println("L1 ASSOC            : " + L1_ASSOC);
			System.out.println("L2 SIZE             : " + L2_SIZE);
			System.out.println("L2 ASSOC            : " + L2_ASSOC);
			System.out.println("REPLACEMENT POLICY  : " + REPLACEMENT_POLICY);
			System.out.println("INCLUSION PROPERTY  : " + INCLUSION_PROPERTY);
			System.out.println("====== L1 contents ======");
			for (int i=0; i<sets; i++) {
				System.out.print("Set\t"+i+" : ");
				for (int j=0; j< L1_ASSOC; j++) {
					if (VALID_BIT[i][j] == 1) {
						System.out.print(L1_CACHE[i][j] + " ");
						if (DIRTY_BIT[i][j] == 1) {
							System.out.print("D\t");
						}else {
							System.out.print(" \t");
						}
					}
				}
				System.out.print("\n");
			}
			System.out.println("====== Simulation results (raw) ======");
			System.out.println("a. number of L1 reads        : " + L1_reads);
			System.out.println("b. number of L1 read misses  : " + L1_read_misses);
			System.out.println("c. number of L1 writes       : " + L1_writes);
			System.out.println("d. number of L1 write misses : " + L1_write_misses);
			float l1_miss_rate = (float) (L1_read_misses + L1_write_misses)/
					(float) (L1_reads + L1_writes);
			System.out.println("e. L1 miss rate              : " + l1_miss_rate);
			System.out.println("f. number of L1 writebacks   : " + L1_writebacks);
						System.out.println("====== Simulation results (raw) ======");
			System.out.println("g. number of L2 reads        : " + 0);
			System.out.println("h. number of L2 read misses  : " + 0);
			System.out.println("i. number of L2 writes       : " + 0);
			System.out.println("j. number of L2 write misses : " + 0);
			System.out.println("k. L2 miss rate              : " + 0);
			System.out.println("l. number of L2 writebacks   : " + 0);
			int traffic = L1_read_misses + L1_write_misses + L1_writebacks;
			System.out.println("m. total memory traffic      : " + traffic);

		} else {
			System.out.println("error occured")
		}
	} // end public
}//end class