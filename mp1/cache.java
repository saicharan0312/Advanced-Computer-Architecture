import java.io.*;
import java.util.*;
class cache {
	public static void main(String[] args) throws Exception {
		if(args.length==8) {
			int BLOCKSIZE = Integer.parseInt(args[0]);
			int L1_SIZE = Integer.parseInt(args[1]);
			int L1_ASSOC = Integer.parseInt(args[2]);
			int L2_SIZE = Integer.parseInt(args[3]);
			int L2_ASSOC = Integer.parseInt(args[4]);
			int REPLACEMENT_POLICY = Integer.parseInt(args[5]);
			int INCLUSION_PROPERTY = Integer.parseInt(args[6]);
			String test_file = args[7];
			System.out.println(test_file);
			String[][] L1_CACHE;
			String[][] tag_and_index_l1;
			String[][] tag_index_offset_l1;      
			int[][] L1_LRU;
			int[][] L1_DIRTY;
			int[][] L1_VALID;
			ArrayList<String> L1_OPTIMAL = new ArrayList<>();
			ArrayList<String> L1_Checking_optimal = new ArrayList<>();
			int L1_SETS=0;
			int L1_TAG=0;
			int L1_INDEX=0;
			int L1_OFFSET=0;
			int L1_READ=0;
			int L1_READ_MISS=0;
			int L1_WRITE=0;
			int L1_WRITE_MISS=0;
			int L1_WRITE_BACK=0;
			int L1_WRITE_BACK_MISS=0;
			float L1_MISS_RATE = 0;
			int L1_COUNT=0;

			String[][] L2_CACHE;
			String[][] tag_and_index_l2;
			String[][] tag_index_offset_l2;
			int[][] L2_LRU;
			int[][] L2_DIRTY;
			int[][] L2_VALID;
			ArrayList<String> L2_OPTIMAL = new ArrayList<>();
			int L2_SETS=0;
			int L2_TAG=0;
			int L2_INDEX=0;
			int L2_OFFSET=0;
			int L2_READ=0;
			int L2_READ_MISS=0;
			int L2_WRITE=0;
			int L2_WRITE_MISS=0;
			int L2_WRITE_BACK=0;
			int L2_WRITE_BACK_MISS=0;
			float L2_MISS_RATE = 0;
			int L2_COUNT=0;

			L1_SETS = (L1_SIZE)/(BLOCKSIZE*L1_ASSOC);
			L1_CACHE = new String[L1_SETS][L1_ASSOC];
			L1_DIRTY = new int[L1_SETS][L1_ASSOC];
			L1_VALID = new int[L1_SETS][L1_ASSOC];
			tag_and_index_l1 = new String[L1_SETS][L1_ASSOC];
			tag_index_offset_l1 = new String[L1_SETS][L1_ASSOC];
			L1_OFFSET = (int)(Math.log(BLOCKSIZE)/Math.log(2));
			L1_INDEX = (int)(Math.log(L1_SETS)/Math.log(2));
			L1_TAG = 32-L1_OFFSET-L1_INDEX;
			L1_LRU = new int[L1_SETS][L1_ASSOC];

            //System.out.println("tag = " +    L1_TAG  +"   index = " +  L1_INDEX  + "  offset = " + L1_OFFSET);
			L2_SETS = (L1_SIZE)/(BLOCKSIZE*L1_ASSOC);
			L2_CACHE = new String[L1_SETS][L1_ASSOC];
			L2_DIRTY = new int[L1_SETS][L1_ASSOC];
			L2_VALID = new int[L1_SETS][L1_ASSOC];
			tag_and_index_l2 = new String[L1_SETS][L1_ASSOC];
			tag_index_offset_l2 = new String[L1_SETS][L1_ASSOC];
			L2_OFFSET = (int)(Math.log(BLOCKSIZE)/Math.log(2));
			L2_INDEX = (int)(Math.log(L1_SETS)/Math.log(2));
			L2_TAG = 32-L1_OFFSET-L1_INDEX;
			L2_LRU = new int[L1_SETS][L1_ASSOC];



			if(REPLACEMENT_POLICY == 0) {
				//L1_LRU = new int[L1_SETS][L1_ASSOC];
			}
			else if(REPLACEMENT_POLICY == 1) {
				L1_LRU = new int[L1_SETS][L1_ASSOC-1];
			}
			else if(REPLACEMENT_POLICY == 2) {
				L1_OPTIMAL = new ArrayList<>();
				L1_Checking_optimal = new ArrayList<>();
				Scanner myReader = null;
				File test_file_copy = new File(test_file);
				try {
					myReader = new Scanner(test_file_copy);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				while (myReader.hasNextLine()) {
					String data = myReader.nextLine();
					String[] data_string = data.split(" ",2);
					int decimal = Integer.parseInt(data_string[1], 16);
					String operation = data_string[0];
					String binary = Integer.toBinaryString(decimal);
				
					if(binary.length() < 32){
						int diff = 32 - binary.length();
						String pad = "";
						for(int i = 0; i < diff; ++i){
							pad = pad.concat("0");
						}
						binary = pad.concat(binary);
					}
					String index = binary.substring(32-L1_INDEX-L1_OFFSET, 32-L1_OFFSET);
					String offset = binary.substring(32-L1_OFFSET, 32);
					String tag = binary.substring(0,32-L1_INDEX-L1_OFFSET);
					
					int decimal_tag = Integer.parseInt(tag,2);
					String hex_tag = Integer.toString(decimal_tag,16);
					String full_length_binaray = tag + index; 
					L1_OPTIMAL.add(hex_tag);
					L1_Checking_optimal.add(full_length_binaray);
				}
			}
			if(L2_SIZE!=0) {
				L2_SETS = (L2_SIZE)/(BLOCKSIZE*L2_ASSOC);
				L2_CACHE = new String[L2_SETS][L2_ASSOC];
				L2_DIRTY = new int[L2_SETS][L2_ASSOC];
				L2_VALID = new int[L2_SETS][L2_ASSOC];
				tag_and_index_l2 = new String[L2_SETS][L2_ASSOC];
				tag_index_offset_l2 = new String[L2_SETS][L2_ASSOC];
				L2_OFFSET = (int)(Math.log(BLOCKSIZE)/Math.log(2));
				L2_INDEX = (int)(Math.log(L2_SETS)/Math.log(2));
				L2_TAG = 32-L2_OFFSET-L2_INDEX;
				//if(REPLACEMENT_POLICY == 0) {
					L2_LRU = new int[L2_SETS][L2_ASSOC];
				//}
				if(REPLACEMENT_POLICY == 1) {
					L2_LRU = new int[L1_SETS][L2_ASSOC-1];
				}
				else if(REPLACEMENT_POLICY == 2) {
					L2_OPTIMAL = new ArrayList<>();
					Scanner myReader = null;
					File test_file_copy = new File(test_file);
					try {
						myReader = new Scanner(test_file_copy);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					while (myReader.hasNextLine()) {
						String data = myReader.nextLine();
						String[] data_string = data.split(" ",2);
						int decimal = Integer.parseInt(data_string[1], 16);
						String operation = data_string[0];
						String binary = Integer.toBinaryString(decimal);
					
						if(binary.length() < 32){
							int diff = 32 - binary.length();
							String pad = "";
							for(int i = 0; i < diff; ++i) {
								pad = pad.concat("0");
							}
							binary = pad.concat(binary);
						}
						String index = binary.substring(32-L2_INDEX-L2_OFFSET, 32-L2_OFFSET);
						String offset = binary.substring(32-L2_OFFSET, 32);
						String tag = binary.substring(0,32-L2_INDEX-L2_OFFSET);
						
						int decimal_tag = Integer.parseInt(tag,2);
						String hex_tag = Integer.toString(decimal_tag,16);
						L2_OPTIMAL.add(hex_tag);
					}
				}
			}

			Scanner myReader = null;
			File test_file_copy = new File(test_file);
			try {
				myReader = new Scanner(test_file_copy);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			int count = 0;
			while (myReader.hasNextLine()) {
				count = count + 1;
				String data = myReader.nextLine();
				String[] data_string = data.split(" ",2);
				//System.out.println(count);
				int decimal = Integer.parseInt(data_string[1], 16);
				String operation = data_string[0];
				String binary = Integer.toBinaryString(decimal);
				if(binary.length() < 32){
					int diff = 32 - binary.length();
					String pad = "";
					for(int i = 0; i < diff; ++i){
						pad = pad.concat("0");
					}
					binary = pad.concat(binary);
				}
				String n_index_l2 = binary.substring(32-L2_INDEX-L2_OFFSET, 32-L2_OFFSET);
				String n_offset_l2 = binary.substring(32-L2_OFFSET, 32);
				String n_tag_l2 = binary.substring(0,32-L2_INDEX-L2_OFFSET);
			
				String index_l1 = binary.substring(32-L1_INDEX-L1_OFFSET, 32-L1_OFFSET);
				String offset_l1 = binary.substring(32-L1_OFFSET, 32);
				String tag_l1 = binary.substring(0,32-L1_INDEX-L1_OFFSET);

				int decimal_tag_l1 = Integer.parseInt(tag_l1,2);
				int n_decimal_tag_l2 = Integer.parseInt(n_tag_l2,2);

				String hex_tag_l1 = Integer.toString(decimal_tag_l1,16);
				String n_hex_tag_l2 = Integer.toString(n_decimal_tag_l2,16);

                int present_decimal_index_l1 = 0;
                if(!index_l1.isEmpty()) {
					present_decimal_index_l1 = Integer.parseInt(index_l1,2);
				}
				int n_present_decimal_index_l2 = 0;
				if(!n_index_l2.isEmpty()) {
					n_present_decimal_index_l2 = Integer.parseInt(n_index_l2,2);
				}

				String tag_l1_and_index_l1 = tag_l1 + index_l1;
				String tag_l1_index_l1_offset_l1 = tag_l1 + index_l1 + offset_l1;

				boolean L1_STATUS = false;
				boolean L2_STATUS = false;
				//System.out.println(L1_Checking_optimal.get(count-1));
				if (operation.charAt(operation.length()-1) == 'w') {
					L1_WRITE = L1_WRITE + 1;
					for (int i=0; i < L1_ASSOC ; i++) {
						if (L1_VALID[present_decimal_index_l1][i] == 1){
							if (L1_CACHE[present_decimal_index_l1][i].equals(hex_tag_l1)) {
								if (REPLACEMENT_POLICY == 0) {
									L1_LRU[present_decimal_index_l1][i] = count;
								}
								else if (REPLACEMENT_POLICY == 1) {
									String x = Integer.toBinaryString(i);
									int l = (int) Math.ceil( Math.log(L1_ASSOC) / Math.log(2) );
									while (x.length() < l) {
										x = "0" + x;
									}
									String[] y = x.split("");
									int f = 1;
									for (int p=0; p<x.length(); p++) {
										L1_LRU[present_decimal_index_l1][f-1] = Integer.parseInt(y[p]);
										if (y[p].equals("0")) {
											f = (2*f);
										}
										else {
											f = (2*f) + 1;
										}
									}
								}
								L1_DIRTY[present_decimal_index_l1][i] = 1;
								L1_STATUS = true;
								break;
							}
						}
					}
					if (L1_STATUS == false) {
						L1_WRITE_MISS = L1_WRITE_MISS + 1;
					}
				}
				else if (operation.charAt(operation.length()-1) == 'r') {
					L1_READ = L1_READ + 1;
					for (int i=0; i < L1_ASSOC ; i++) {
						if (L1_VALID[present_decimal_index_l1][i] == 1){
							if (L1_CACHE[present_decimal_index_l1][i].equals(hex_tag_l1)) {
								if (REPLACEMENT_POLICY == 0) {
									L1_LRU[present_decimal_index_l1][i] = count;
								}
								else if (REPLACEMENT_POLICY == 1) {
									String x = Integer.toBinaryString(i);
									int l = (int) Math.ceil( Math.log(L1_ASSOC) / Math.log(2) );
									while (x.length() < l) {
										x = "0" + x;
									}
									String[] y = x.split("");
									int f = 1;
									for (int p=0; p<x.length(); p++) {
										L1_LRU[present_decimal_index_l1][f-1] = Integer.parseInt(y[p]);
										if (y[p].equals("0")) {
											f = (2*f);
										}
										else {
											f = (2*f) + 1;
										}
									}
								}
								L1_STATUS = true;
								break;
							}
						}
					}
					if (L1_STATUS == false) {
						L1_READ_MISS = L1_READ_MISS + 1;
					}
				}
				if (L1_STATUS == false && L2_SIZE == 0) {
					L1_STATUS = false;
					for (int i = 0; i < L1_ASSOC ; i++) {
						if (L1_VALID[present_decimal_index_l1][i] == 0) {
							L1_CACHE[present_decimal_index_l1][i] = hex_tag_l1;
							tag_and_index_l1[present_decimal_index_l1][i] = tag_l1_and_index_l1;
							tag_index_offset_l1[present_decimal_index_l1][i] = tag_l1_index_l1_offset_l1;
							L1_VALID[present_decimal_index_l1][i] = 1;
							L1_STATUS = true;
							if (REPLACEMENT_POLICY == 0) {
								L1_LRU[present_decimal_index_l1][i] = count;
							}
							else if (REPLACEMENT_POLICY == 1) {
								String x = Integer.toBinaryString(i);
								int l = (int) Math.ceil( Math.log(L1_ASSOC) / Math.log(2) );
								while (x.length() < l) {
									x = "0" + x;
								}
								String[] y = x.split("");
								int f = 1;
								for (int p=0; p<x.length(); p++) {
									L1_LRU[present_decimal_index_l1][f-1] = Integer.parseInt(y[p]);
									if (y[p].equals("0")) {
										f = (2*f);
									}
									else {
										f = (2*f) + 1;
									}
								}
							}
							if(operation.charAt(operation.length()-1) == 'w') {
								L1_DIRTY[present_decimal_index_l1][i] = 1;
							}
							else {
								L1_DIRTY[present_decimal_index_l1][i] = 0;
							}
							break;
						}
					}
					if (L1_STATUS == false) {
						int replacement_index = 0;
						if (REPLACEMENT_POLICY == 0) {
							int LRU_Index = 0;
							for (int i=0 ; i < L1_ASSOC ; i++) {
								if (L1_LRU[present_decimal_index_l1][i] < L1_LRU[present_decimal_index_l1][LRU_Index]) {
									LRU_Index = i;
								}
							}
							replacement_index = LRU_Index;
						} else if (REPLACEMENT_POLICY == 1) {
							String LRU_Direction = "";
							int i = 1;
							while(i-1 < L1_LRU[0].length) {
								LRU_Direction += L1_LRU[present_decimal_index_l1][i-1];
								if (L1_LRU[present_decimal_index_l1][i-1] == 0) {
									i = (2*i) + 1;
								} else {
									i = 2*i;
								}
							}
							String[] test_and_flip = LRU_Direction.split("");
							String replacement_index_binary = "";
							for (int j=0; j<LRU_Direction.length(); j++) {
								if (test_and_flip[j].equals("0")){
									replacement_index_binary =  replacement_index_binary + "1";
								}
								else {
									replacement_index_binary = replacement_index_binary + "0";
								}
							}
							replacement_index = Integer.parseInt(replacement_index_binary,2);
						} else if (REPLACEMENT_POLICY == 2){
							int[] temp = new int[L1_ASSOC];
							int c = 0;
							int find = 0;
							for (int j=0; j<L1_ASSOC; j++){
								for (int i=count-1; i<L1_Checking_optimal.size(); i++) {
									if (tag_and_index_l1[present_decimal_index_l1][j].equals(L1_Checking_optimal.get(i))) {
										temp[j] = i;
										c++;
										break;
										}
									}
								if (c == j) {
									replacement_index = j;
									find =1;
								}
							}
							if(find == 0) {
								int max_idx = 0;
								for (int k=0; k<L1_ASSOC; k++) {
									if (temp[k] > temp[max_idx]) {
										max_idx = k;
									}
								}
								replacement_index = max_idx;
							}
						}

						if (L1_DIRTY[present_decimal_index_l1][replacement_index] == 1){
							L1_WRITE_BACK = L1_WRITE_BACK + 1;
						}
						L1_CACHE[present_decimal_index_l1][replacement_index] = hex_tag_l1;
						L1_VALID[present_decimal_index_l1][replacement_index] = 1;
						tag_and_index_l1[present_decimal_index_l1][replacement_index] = tag_l1_and_index_l1;
						tag_index_offset_l1[present_decimal_index_l1][replacement_index] = tag_l1_index_l1_offset_l1;
						if (REPLACEMENT_POLICY == 0) {
							L1_LRU[present_decimal_index_l1][replacement_index] = count;
						}
						else if (REPLACEMENT_POLICY == 1) {
							String x = Integer.toBinaryString(replacement_index);
							int l = (int) Math.ceil( Math.log(L1_ASSOC) / Math.log(2) );
							while (x.length() < l) {
								x = "0" + x;
							}
							String[] y = x.split("");
							int f = 1;
							for (int p=0; p<x.length(); p++) {
								L1_LRU[present_decimal_index_l1][f-1] = Integer.parseInt(y[p]);
								if (y[p].equals("0")) {
									f = (2*f);
								}
								else {
									f = (2*f) + 1;
								}
							}
						}
						if (operation.charAt(operation.length()-1) == 'w') {
							L1_DIRTY[present_decimal_index_l1][replacement_index] = 1;
						}else {
							L1_DIRTY[present_decimal_index_l1][replacement_index] = 0;
						}
					}
				} 
				else if (L1_STATUS == false && L2_SIZE!=0) {
					L2_COUNT = L2_COUNT + 1;
					L1_STATUS = false;
					String replacement_cache = "";
					String replacement_cache_with_offset = "";
					for (int i = 0; i < L1_ASSOC ; i++) {
						if (L1_VALID[present_decimal_index_l1][i] == 0) {
							L1_CACHE[present_decimal_index_l1][i] = hex_tag_l1;
							tag_and_index_l1[present_decimal_index_l1][i] = tag_l1_and_index_l1;
							tag_index_offset_l1[present_decimal_index_l1][i] = tag_l1_index_l1_offset_l1;
							L1_VALID[present_decimal_index_l1][i] = 1;
							L1_STATUS = true;
							if (REPLACEMENT_POLICY == 0) {
								L1_LRU[present_decimal_index_l1][i] = count;
							}
							else if (REPLACEMENT_POLICY == 1) {
								String x = Integer.toBinaryString(i);
								int l = (int) Math.ceil( Math.log(L1_ASSOC) / Math.log(2) );
								while (x.length() < l) {
									x = "0" + x;
								}
								String[] y = x.split("");
								int f = 1;
								for (int p=0; p<x.length(); p++) {
									L1_LRU[present_decimal_index_l1][f-1] = Integer.parseInt(y[p]);
									if (y[p].equals("0")) {
										f = (2*f);
									}
									else {
										f = (2*f) + 1;
									}
								}
							}
							if(operation.charAt(operation.length()-1) == 'w') {
								L1_DIRTY[present_decimal_index_l1][i] = 1;
							}
							else {
								L1_DIRTY[present_decimal_index_l1][i] = 0;
							}
							break;
						}
					}
					if (L1_STATUS == false) {
						int replacement_index = 0;
						if (REPLACEMENT_POLICY == 0) {
							int LRU_Index = 0;
							for (int i=0 ; i < L1_ASSOC ; i++) {
								if (L1_LRU[present_decimal_index_l1][i] < L1_LRU[present_decimal_index_l1][LRU_Index]) {
									LRU_Index = i;
								}
							}
							replacement_index = LRU_Index;
						} else if (REPLACEMENT_POLICY == 1) {
							String LRU_Direction = "";
							int i = 1;
							while(i-1 < L1_LRU[0].length) {
								LRU_Direction += L1_LRU[present_decimal_index_l1][i-1];
								if (L1_LRU[present_decimal_index_l1][i-1] == 0) {
									i = (2*i) + 1;
								} else {
									i = 2*i;
								}
							}
							String[] test_and_flip = LRU_Direction.split("");
							String replacement_index_binary = "";
							for (int j=0; j<LRU_Direction.length(); j++) {
								if (test_and_flip[j].equals("0")){
									replacement_index_binary =  replacement_index_binary + "1";
								}
								else {
									replacement_index_binary = replacement_index_binary + "0";
								}
							}
							replacement_index = Integer.parseInt(replacement_index_binary,2);
						} else if (REPLACEMENT_POLICY == 2){
							int[] temp = new int[L1_ASSOC];
							int c = 0;
							int find = 0;
							for (int j=0; j<L1_ASSOC; j++){
								for (int i=count; i<L1_Checking_optimal.size(); i++) {
									if (tag_and_index_l1[present_decimal_index_l1][j].equals(L1_Checking_optimal.get(i))) {
										temp[j] = i;
										c++;
										break;
										}
									}
								if (c == j) {
									replacement_index = j;
									find =1;
								}
							}
							if(find == 0) {
								int max_idx = 0;
								for (int k=0; k<L1_ASSOC; k++) {
									if (temp[k] > temp[max_idx]) {
										max_idx = k;
									}
								}
								replacement_index = max_idx;
							}
						}

						if (L1_DIRTY[present_decimal_index_l1][replacement_index] == 1){
							L1_WRITE_BACK = L1_WRITE_BACK + 1;
							replacement_cache = tag_and_index_l1[present_decimal_index_l1][replacement_index];
							replacement_cache_with_offset = tag_index_offset_l1[present_decimal_index_l1][replacement_index];
						}
						L1_CACHE[present_decimal_index_l1][replacement_index] = hex_tag_l1;
						L1_VALID[present_decimal_index_l1][replacement_index] = 1;
						
						tag_and_index_l1[present_decimal_index_l1][replacement_index] = tag_l1_and_index_l1;
						tag_index_offset_l1[present_decimal_index_l1][replacement_index] = tag_l1_index_l1_offset_l1;

						if (REPLACEMENT_POLICY == 0) {
							L1_LRU[present_decimal_index_l1][replacement_index] = count;
						}
						else if (REPLACEMENT_POLICY == 1) {
							String x = Integer.toBinaryString(replacement_index);
							int l = (int) Math.ceil( Math.log(L1_ASSOC) / Math.log(2) );
							while (x.length() < l) {
								x = "0" + x;
							}
							String[] y = x.split("");
							int f = 1;
							for (int p=0; p<x.length(); p++) {
								L1_LRU[present_decimal_index_l1][f-1] = Integer.parseInt(y[p]);
								if (y[p].equals("0")) {
									f = (2*f);
								}
								else {
									f = (2*f) + 1;
								}
							}
						}
						if (operation.charAt(operation.length()-1) == 'w') {
							L1_DIRTY[present_decimal_index_l1][replacement_index] = 1;
						}else {
							L1_DIRTY[present_decimal_index_l1][replacement_index] = 0;
						}
					}
					//L1 to L2 write back
					if (!replacement_cache_with_offset.isEmpty()) {
						L2_STATUS = false;
						String to_put_index_l2 = replacement_cache_with_offset.substring(32-L2_INDEX-L2_OFFSET, 32-L2_OFFSET);
						String to_put_offset_l2 = replacement_cache_with_offset.substring(32-L2_OFFSET, 32);
						String to_put_tag_l2 = replacement_cache_with_offset.substring(0,32-L2_INDEX-L2_OFFSET);

						String tag_l2_index_l2 = to_put_tag_l2 + to_put_index_l2;
						String tag_l2_index_l2_offset_l2 = to_put_tag_l2 + to_put_index_l2 + to_put_offset_l2;

						int decimal_tag_l2 = Integer.parseInt(to_put_tag_l2,2);
						String hex_tag_l2 = Integer.toString(decimal_tag_l2,16);

						int present_decimal_index_l2 = Integer.parseInt(to_put_index_l2,2);
						//System.out.println("hex_tag_12 = " + hex_tag_l2 );
						//System.out.println("PRE = " + present_decimal_index_l2 );
						L2_WRITE = L2_WRITE + 1;
						for (int i=0; i < L2_ASSOC ; i++) {
							if (L2_VALID[present_decimal_index_l2][i] == 1){
								if (L2_CACHE[present_decimal_index_l2][i].equals(hex_tag_l2)) {
									if (REPLACEMENT_POLICY == 0) {
										L2_LRU[present_decimal_index_l2][i] = L2_COUNT;
										L2_COUNT = L2_COUNT + 1;
									}
									L2_DIRTY[present_decimal_index_l2][i] = 1;
									L2_STATUS = true;
									break;
								}
							}
						}
						if (L2_STATUS == false) {
							L2_WRITE_MISS = L2_WRITE_MISS + 1;
						}
						if (L2_STATUS == false) {
							L2_STATUS = false;
							for (int i = 0; i < L2_ASSOC ; i++) {
								if (L2_VALID[present_decimal_index_l2][i] == 0) {
									L2_CACHE[present_decimal_index_l2][i] = hex_tag_l2;
									tag_and_index_l2[present_decimal_index_l2][i] = tag_l2_index_l2;
									tag_index_offset_l2[present_decimal_index_l2][i] = tag_l2_index_l2_offset_l2;
									L2_VALID[present_decimal_index_l2][i] = 1;
									L2_STATUS = true;
									if (REPLACEMENT_POLICY == 0) {
										L2_LRU[present_decimal_index_l2][i] = L2_COUNT;
										L2_COUNT  = L2_COUNT + 1;
									}
									L2_DIRTY[present_decimal_index_l2][i] = 1;
									break;
								}
							}
							if (L2_STATUS == false) {
								int replacement_index_l2 = 0;
								if (REPLACEMENT_POLICY == 0) {
									int LRU2_Index = 0;
									for (int i=0 ; i < L2_ASSOC ; i++) {
										if (L2_LRU[present_decimal_index_l2][i] < L2_LRU[present_decimal_index_l2][LRU2_Index]) {
											LRU2_Index = i;
										}
									}
									replacement_index_l2 = LRU2_Index;
								}  
								if (L2_DIRTY[present_decimal_index_l2][replacement_index_l2] == 1){
									L2_WRITE_BACK = L2_WRITE_BACK + 1;
								}
								//System.out.println("P = " + present_decimal_index_l2 + "   RI = " +  replacement_index_l2);
								L2_CACHE[present_decimal_index_l2][replacement_index_l2] = hex_tag_l2;
								L2_VALID[present_decimal_index_l2][replacement_index_l2] = 1;
								tag_and_index_l2[present_decimal_index_l2][replacement_index_l2] = tag_l2_index_l2;
								tag_index_offset_l2[present_decimal_index_l2][replacement_index_l2] = tag_l2_index_l2_offset_l2;
								if (REPLACEMENT_POLICY == 0) {
									L2_LRU[present_decimal_index_l2][replacement_index_l2] = L2_COUNT;
									L2_COUNT = L2_COUNT + 1;
								}
								L2_DIRTY[present_decimal_index_l2][replacement_index_l2] = 1;
							}
						}
					}

					L2_STATUS = false;
					L2_READ = L2_READ + 1;
					for (int i=0; i < L2_ASSOC ; i++) {
						if (L2_VALID[n_present_decimal_index_l2][i] == 1){
							if (L2_CACHE[n_present_decimal_index_l2][i].equals(n_hex_tag_l2)) {
								if (REPLACEMENT_POLICY == 0) {
									L2_LRU[n_present_decimal_index_l2][i] = L2_COUNT; 
									L2_COUNT = L2_COUNT + 1;
								}
								L2_STATUS = true;
								break;
							}
						}
					}
					if (L2_STATUS == false) {
						L2_READ_MISS = L2_READ_MISS + 1;
					}
					// L2 read miss
					if (L2_STATUS == false){
						L2_STATUS = false;
						String l2_replacement_cache = "";
						int l2_replacement_index = 0;
						for (int i = 0; i < L2_ASSOC ; i++) {
							if (L2_VALID[n_present_decimal_index_l2][i] == 0) {
								L2_CACHE[n_present_decimal_index_l2][i] = n_hex_tag_l2;
								tag_and_index_l2[n_present_decimal_index_l2][i] = n_tag_l2 + n_index_l2;
								tag_index_offset_l2[n_present_decimal_index_l2][i] = n_tag_l2 + n_index_l2 + n_offset_l2;
								L2_VALID[n_present_decimal_index_l2][i] = 1;
								L2_STATUS = true;
								if (REPLACEMENT_POLICY == 0) {
									L2_LRU[n_present_decimal_index_l2][i] = L2_COUNT;
									L2_COUNT = L2_COUNT + 1;
								}
								//if (operation.charAt(operation.length()-1) == 'w') {
								//	L2_DIRTY[n_present_decimal_index_l2][i] = 1;
								//}
								//else {
									L2_DIRTY[n_present_decimal_index_l2][i] = 0;
								//}
								break;
							}
						}
						if (L2_STATUS == false) {
							l2_replacement_index = 0;
							if (REPLACEMENT_POLICY == 0) {
								int find_lru = 0;
								for (int i=0 ; i < L2_ASSOC ; i++) {
									if (L2_LRU[n_present_decimal_index_l2][i] < L2_LRU[n_present_decimal_index_l2][find_lru]) {
										find_lru = i;
									}
								}
								l2_replacement_index = find_lru;
							}

							if (L2_DIRTY[n_present_decimal_index_l2][l2_replacement_index] == 1){
								L2_WRITE_BACK = L2_WRITE_BACK + 1;
								l2_replacement_cache = tag_index_offset_l2[n_present_decimal_index_l2][l2_replacement_index];
							}
							L2_CACHE[n_present_decimal_index_l2][l2_replacement_index] = n_hex_tag_l2;
							tag_and_index_l2[n_present_decimal_index_l2][l2_replacement_index] = n_tag_l2 + n_index_l2;
							tag_index_offset_l2[n_present_decimal_index_l2][l2_replacement_index] = n_tag_l2 + n_index_l2 + n_offset_l2;
							L2_VALID[n_present_decimal_index_l2][l2_replacement_index] = 1;

							if (REPLACEMENT_POLICY == 0) {
								L2_LRU[n_present_decimal_index_l2][l2_replacement_index] = L2_COUNT;
								L2_COUNT = L2_COUNT + 1;
							}
							//if (operation.charAt(operation.length()-1) == 'w') {
							//	L2_DIRTY[n_present_decimal_index_l2][l2_replacement_index] = 1;
							//}else {
								L2_DIRTY[n_present_decimal_index_l2][l2_replacement_index] = 0;
							//}
						}




						// For Inclusion cache
						if (!l2_replacement_cache.isEmpty() && INCLUSION_PROPERTY == 1) {
							String to_put_index_inclusion = l2_replacement_cache.substring(32-L1_INDEX-L1_OFFSET, 32-L1_OFFSET);
							String to_put_offset_inclusion = l2_replacement_cache.substring(32-L1_OFFSET, 32);
							String to_put_tag_inclusion = l2_replacement_cache.substring(0,32-L1_INDEX-L1_OFFSET);

							String tag_inclusion_index_inclusion = to_put_tag_inclusion + to_put_index_inclusion;
							String tag_inclusion_index_l2_offset_inclusion = to_put_tag_inclusion + to_put_index_inclusion + to_put_offset_inclusion;

							int decimal_tag_inclusion = Integer.parseInt(to_put_tag_inclusion,2);
							String hex_tag_inclusion = Integer.toString(decimal_tag_inclusion,16);

							int present_decimal_index_inclsion = Integer.parseInt(to_put_index_inclusion,2);
							int need_to_write_back_miss = 0;
							for (int i=0; i<L1_ASSOC; i++) {
								if (L1_CACHE[present_decimal_index_inclsion][i].equals(hex_tag_inclusion)) {
										L1_VALID[present_decimal_index_inclsion][i] = 0;
										need_to_write_back_miss = L1_DIRTY[present_decimal_index_inclsion][i];
										break;
								}
							}
							if (need_to_write_back_miss == 1) {
								L2_WRITE_BACK_MISS = L2_WRITE_BACK_MISS + 1;
							}
						}
					}
				}
			}

			String policy = "";
			String property = "";
			if(REPLACEMENT_POLICY == 0) {
				policy = "LRU";
			} else if(REPLACEMENT_POLICY == 1) {
				policy = "Psuedo LRU";
			} else if(REPLACEMENT_POLICY == 2) {
				policy = "Optimal";
			}
			if(INCLUSION_PROPERTY == 0) {
				property = "non inclusive";
			} else if(INCLUSION_PROPERTY == 1) {
				property = "inclusion";
			}
			System.out.println("======== Simulator configuration ======= ");
			System.out.println("BLOCKSIZE:                  " + BLOCKSIZE);
			System.out.println("L1 SIZE:                    " + L1_SIZE);
			System.out.println("L1 ASSOC:                   " + L1_ASSOC);
			System.out.println("L2 SIZE:                    " + L2_SIZE);
			System.out.println("L2 ASSOC:                   " + L2_ASSOC);
			System.out.println("REPLACEMENT_POLICY:         " + policy);
			System.out.println("INCLUSION_PROPERTY:         " + property);
			System.out.println("============ L1 contents ===============");
			for (int i=0; i<L1_SETS; i++) {
				System.out.print("Set\t"+i+":   ");
				for (int j=0; j< L1_ASSOC; j++) {
					if (L1_VALID[i][j] == 1) {
						System.out.print(L1_CACHE[i][j] + "\t");
						if (L1_DIRTY[i][j] == 1) {
							System.out.print("D\t");
						}else {
							System.out.print("\t");
						}
					}
				}
				System.out.print("\n");
			}
			if(L2_SIZE!=0) {
				System.out.println("=========== L2 contents ===========");
				for (int i=0; i<L2_SETS; i++) {
					System.out.print("Set\t"+i+":\t");
					for (int j=0; j< L2_ASSOC; j++) {
						if (L2_VALID[i][j] == 1) {
							System.out.print(L2_CACHE[i][j] + " ");
							if (L2_DIRTY[i][j] == 1) {
								System.out.print("D\t");
							}else {
								System.out.print("\t");
							}
						}
					}
					System.out.print("\n");
				}
			}
			System.out.println("====== Simulation results (raw) ======");
			System.out.println("a. number of L1 reads        : " + L1_READ);
			System.out.println("b. number of L1 read misses  : " + L1_READ_MISS);
			System.out.println("c. number of L1 writes       : " + L1_WRITE);
			System.out.println("d. number of L1 write misses : " + L1_WRITE_MISS);
			L1_MISS_RATE = (float) (L1_READ_MISS + L1_WRITE_MISS)/
					(float) (L1_READ + L1_WRITE);
			System.out.println("e. L1 miss rate              : " + L1_MISS_RATE);
			System.out.println("f. number of L1 writebacks   : " + L1_WRITE_BACK);
			int TRAFFIC = L1_READ_MISS + L1_WRITE_MISS + L1_WRITE_BACK;

			if (L2_SIZE == 0) {
				System.out.println("g. number of L2 reads        : " + L2_READ);
				System.out.println("h. number of L2 read misses  : " + L2_READ_MISS);
				System.out.println("i. number of L2 writes       : " + L2_WRITE);
				System.out.println("j. number of L2 write misses : " + L2_WRITE_MISS);
				System.out.println("k. L2 miss rate              : " + L2_MISS_RATE);
				System.out.println("l. number of L2 writebacks   : " + L2_WRITE_BACK);
			}
			if (L2_SIZE != 0) {
				System.out.println("g. number of L2 reads        : " + L2_READ);
				System.out.println("h. number of L2 read misses  : " + L2_READ_MISS);
				System.out.println("i. number of L2 writes       : " + L2_WRITE);
				System.out.println("j. number of L2 write misses : " + L2_WRITE_MISS);
				L2_MISS_RATE = (float) (L2_READ_MISS)/ (float) (L2_READ);
				System.out.println("k. L2 miss rate              : " + L2_MISS_RATE);
				System.out.println("l. number of L2 writebacks   : " + L2_WRITE_BACK);
				TRAFFIC = L2_READ_MISS + L2_WRITE_MISS + L2_WRITE_BACK + L1_WRITE_BACK_MISS;
			}
			System.out.println("m. total memory traffic      : " + TRAFFIC);
		} else {
			throw new Exception("Something went wrong Please try again");
		}
	}
}