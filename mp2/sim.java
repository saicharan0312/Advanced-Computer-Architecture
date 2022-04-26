import java.util.*;
import java.io.*;
class sim {
	public static void main(String[] args) {
		int low = 0;
		int high = 0;
		int mid = 0;
		int total_count = 0;
		int count = 0;
		int miss_count = 0;
		String type = args[0];
		int counter_bits = Integer.parseInt(args[1]);
		// 4 command line argumenets for g-share
		if(args.length == 4) 
		{
			System.out.println("gshare");
			int operator_bits = Integer.parseInt(args[2]);
			String trace_file = args[3];
			String gshare_manipulation = "";
			high = 7;
			low = 0;
			mid = 4;
			int size = (int) Math.pow(2, counter_bits );
			int[] gshare_count_bits = new int[size];
			Arrays.fill(gshare_count_bits , mid);
			for(int i = 0; i< operator_bits ; i++) 
			{
				gshare_manipulation = gshare_manipulation + "0";
			}
			try 
				{
					File myObj = new File(trace_file);
					Scanner myReader = new Scanner(myObj);
					while (myReader.hasNextLine()) 
					{
						total_count = total_count + 1;
						String data = myReader.nextLine();
						String[] data1 = data.split(" ");
						String income = data1[1];
						String hex_value = data1[0];

						int hex_to_dec = Integer.parseInt(hex_value, 16);
						String dec_to_bin = Integer.toBinaryString(hex_to_dec);

						int bin_len = dec_to_bin.length();

						int kk = bin_len;
						//System.out.println(kk);
						while(kk<24) {
							dec_to_bin = "0" + dec_to_bin;
							kk = kk + 1;
							//System.out.println(kk);
							//System.out.println(dec_to_bin);
						}
						//System.out.println(dec_to_bin);
						String get_index_in_binary_1 = dec_to_bin.substring(bin_len-counter_bits, bin_len-operator_bits);
						String get_index_in_binary_to_convert = dec_to_bin.substring(bin_len-operator_bits, bin_len);

						String get_index_in_binary_2 = "";

						int run_time = get_index_in_binary_to_convert.length();

                        int r = 0;
						while(r<run_time)
						{
							if(get_index_in_binary_to_convert.charAt(r) == gshare_manipulation.charAt(r)) 
							{
								//System.out.println("yes");
								get_index_in_binary_2 = get_index_in_binary_2 + "0";
							}
							else
							{
								get_index_in_binary_2 = get_index_in_binary_2 + "1";
							}
							r = r + 1;
						}

                        String final_get_index_in_binary = get_index_in_binary_1 + get_index_in_binary_2;
						int get_index_value = Integer.parseInt(final_get_index_in_binary,2);
						//System.out.println("index = " + get_index_value + "   sor = " + get_index_in_binary_to_convert);
						String predicted;
						if(gshare_count_bits[get_index_value] < mid) 
						{
							predicted = "n";
						}  
						else 
						{
							predicted = "t";
						}
						if(!income.equals(predicted)) 
						{
							miss_count = miss_count + 1;
						}
						if(income.equals("t"))
						{	
							if(gshare_count_bits[get_index_value] != high)
							{
								gshare_count_bits[get_index_value] = gshare_count_bits[get_index_value] + 1;
							}
							gshare_manipulation = "1" + gshare_manipulation;
							gshare_manipulation = gshare_manipulation.substring(0, gshare_manipulation.length()-1);
						}
						else
						{
							if(gshare_count_bits[get_index_value] != low)
							{
								gshare_count_bits[get_index_value] = gshare_count_bits[get_index_value] - 1;
							}
							gshare_manipulation = "0" + gshare_manipulation;
							gshare_manipulation = gshare_manipulation.substring(0, gshare_manipulation.length()-1);
						}
					}
					myReader.close();
				} catch (FileNotFoundException e) 
				{
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				double val = (  (  (double)miss_count/(double)total_count    )  *10000.0);
				
				double missrate = Math.round(val)/100.0;
				System.out.println("OUTPUT");
				System.out.println("number of prediction      :" + total_count);
				System.out.println("number of misprediction   :" + miss_count);
				System.out.println("misprediction rate        :" + missrate + "%");
				for(int i = 0; i<gshare_count_bits.length; i++) 
				{
					System.out.println(i + "   " + gshare_count_bits[i]);
				}
		}
		// 6 command line arguments for hybrid
		else if(args.length == 6) 
		{
			// all declarations
			high = 3;
			low = 0;
			mid = 2;
			total_count = 0;
			miss_count = 0;
			int b_high = 7;
			int b_low = 0;
			int b_mid = 4;
			int g_high = 7;
			int g_low = 0;
			int g_mid = 4;
			String g_gshare_str = "";
			int g_m_bits = Integer.parseInt(args[2]);
			int g_n_bits = Integer.parseInt(args[3]);
			int b_mb_bits = Integer.parseInt(args[4]);
			String trace_file = args[5];
			int b_size = (int) Math.pow(2, b_mb_bits);
			int g_size = (int) Math.pow(2, g_m_bits);
			int h_size = (int) Math.pow(2, counter_bits);
			int[] h_hybrid = new int[h_size]; 
			int[] g_gshare = new int[g_size];
			int[] b_bimodal = new int[b_size];
			Arrays.fill(g_gshare, g_mid);
			Arrays.fill(b_bimodal,b_mid);
			Arrays.fill(h_hybrid, 1);
			for(int i=0;i<g_n_bits;i++) 
			{
				g_gshare_str = g_gshare_str + "0";
			}
			// start reading the file
			try 
			{
				File myObj = new File(trace_file);
				Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) 
				{
					total_count =  total_count + 1;
					String data1 = myReader.nextLine();
					String[] data = data1.split(" ");
					String income = data[1];
					int h_dec = Integer.parseInt(data[0], 16);
					String h_bin = Integer.toBinaryString(h_dec);
					String h_bin_index = h_bin.substring(h_bin.length()-2-counter_bits, h_bin.length()-2);
					int h_index = Integer.parseInt(h_bin_index, 2);

					// calculate the prediction for bimodal
					int b_dec = Integer.parseInt(data[0], 16);
					String b_bin = Integer.toBinaryString(b_dec);
					String b_bin_index = b_bin.substring(b_bin.length()-2-b_mb_bits, b_bin.length()-2);
					int b_index = Integer.parseInt(b_bin_index,2);
					String b_pred = "";
					if(b_bimodal[b_index] < b_mid ) 
					{
						b_pred = "n";
					}
					else 
					{
						b_pred = "t";
					}
					// end bimodal
					// calculate the prediction for g-share
					String g_pred = "";
					int g_dec = Integer.parseInt(data[0], 16);
					String g_bin = Integer.toBinaryString(g_dec);
					String g_ind1 = g_bin.substring(g_bin.length()-2-g_m_bits, g_bin.length()-2-g_n_bits);
					String g_find_ind2 = g_bin.substring(g_bin.length()-2-g_n_bits, g_bin.length()-2);
					String g_ind2 = "";
					String[] gs_gshare = g_gshare_str.split("");
					String[] gs_find_ind2 = g_find_ind2.split("");
					int l = gs_gshare.length;
					for(int i=0;i<l;i++) 
					{
						if(gs_find_ind2[i].equals(gs_gshare[i]))
						{
							g_ind2 = g_ind2 + "0";
						}
						else
						{
							g_ind2 = g_ind2 + "1";
						}
					}
					String g_bin_ind = g_ind1 + g_ind2;
					int g_index = Integer.parseInt(g_bin_ind , 2);
					if(g_gshare[g_index] < g_mid)
					{
						g_pred = "n";
					}
					else
					{
						g_pred = "t";
					}
					// end g-share 

					// update hybrid array with update the of counter of both gshare and bimodal

					if(h_hybrid[h_index]>=mid) 
					{
						if(!income.equals(g_pred))
						{
							miss_count = miss_count + 1;
						}
						// gshare update start
						if(income.equals("t"))
						{
							if(g_gshare[g_index]!=g_high)
							{
								g_gshare[g_index] = g_gshare[g_index] + 1;
							}
						}
						else
						{
							if(g_gshare[g_index]!=g_low)
							{
								g_gshare[g_index] = g_gshare[g_index] - 1;
							}
						}
						// gshare update end
					}
					else
					{
						if(!income.equals(b_pred))
						{
							miss_count = miss_count + 1;
						}
						// bimodal update start
						if(income.equals("t"))
						{
							if(b_bimodal[b_index]!=b_high)
							{
								b_bimodal[b_index] = b_bimodal[b_index] + 1;
							}
						}
						else
						{
							if(b_bimodal[b_index]!=b_low)
							{
								b_bimodal[b_index] = b_bimodal[b_index] - 1;
							}
						// bimodal update end
					    }
					}
					// update xor start
					if(income.equals("t"))
					{
						g_gshare_str = "1" + g_gshare_str;
					}
					else
					{
						g_gshare_str = "0" + g_gshare_str;
					}
					g_gshare_str = g_gshare_str.substring(0, g_gshare_str.length()-1);
					// update xor end
					// update the picker table
					if(b_pred.equals(income) && !g_pred.equals(income))
					{
						if(h_hybrid[h_index]!=low)
						{
							h_hybrid[h_index] = h_hybrid[h_index] - 1;
						}
					}
					if(!b_pred.equals(income) && g_pred.equals(income))
					{
						if(h_hybrid[h_index]!=high)
						{
							h_hybrid[h_index] = h_hybrid[h_index] + 1;
						}
					}	
					// updte end of picker table
					// end
				}
				myReader.close();
			} 
			catch (FileNotFoundException e) 
			{
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
			// end try catch
			// print value start
			double val = (  (  (double)miss_count/(double)total_count    )  *10000.0);
			
			double missrate = Math.round(val)/100.0;
			System.out.println("OUTPUT");
			System.out.println("number of prediction      :" + total_count);
			System.out.println("number of misprediction   :" + miss_count);
			System.out.println("misprediction rate        :" + missrate + "%");
			System.out.println("FINAL CHOOSER CONTENTS");
			for(int i = 0; i<h_hybrid.length; i++) 
			{
				System.out.println(i + "   " + h_hybrid[i]);
			}
			System.out.println("FINAL GSHARE CONTENTS");
			for(int i = 0; i<g_gshare.length; i++) 
			{
				System.out.println(i + "   " + g_gshare[i]);
			}
			System.out.println("FINAL BIMODAL CONTENTS");
			for(int i = 0; i<b_bimodal.length; i++) 
			{
				System.out.println(i + "   " + b_bimodal[i]);
			}
			// print value end
		} 
		// 3 command line arguments for smith and bimodel
		else if(args.length == 3)
		{
			if(type.equals("smith"))
			{
				high = (int)(Math.pow(2, counter_bits ) - 1);
				count = (high + 1) /2;
				mid = count;
				String trace_file = args[2];
				try 
				{
					File myObj = new File(trace_file);
					Scanner myReader = new Scanner(myObj);
					while (myReader.hasNextLine()) 
					{
						total_count = total_count + 1;
						String data = myReader.nextLine();
						String[] data1 = data.split(" ");
						String income = data1[1];
						String predicted;
						if(count < mid) 
						{
							predicted = "n";
						}  
						else 
						{
							predicted = "t";
						}
						if(!income.equals(predicted)) 
						{
							miss_count = miss_count + 1;
						}
						if(income.equals("t"))
						{
							if(count != high)
							{
								count = count + 1;
							}
						}
						else
						{
							if(count != low)
							{
								count = count - 1;
							}
						}
					}
					myReader.close();
				} catch (FileNotFoundException e) 
				{
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				double val = (  (  (double)miss_count/(double)total_count    )  *10000.0);
				
				double missrate = Math.round(val)/100.0;
				System.out.println("OUTPUT");
				System.out.println("number of prediction      :" + total_count);
				System.out.println("number of misprediction   :" + miss_count);
				System.out.println("misprediction rate        :" + missrate + "%");
				System.out.println("FINAL COUNTER CONTENT     :" + count);
			}
			else 
			{
				high = 7;
				low = 0;
				mid = 4;
				int size = (int) Math.pow(2, counter_bits);
				int[] bimodel_count_bits = new int[size];
				Arrays.fill(bimodel_count_bits, mid);
				String trace_file = args[2];
				try 
				{
					File myObj = new File(trace_file);
					Scanner myReader = new Scanner(myObj);
					while (myReader.hasNextLine()) 
					{
						total_count = total_count + 1;
						String data = myReader.nextLine();
						String[] data1 = data.split(" ");
						String income = data1[1];
						String hex_value = data1[0];
						int hex_to_dec = Integer.parseInt(hex_value, 16);
						String dec_to_bin = Integer.toBinaryString(hex_to_dec);
						int bin_len = dec_to_bin.length();
						String get_index_in_binary = dec_to_bin.substring(bin_len-2-counter_bits, bin_len-2);
						int get_index_value = Integer.parseInt(get_index_in_binary,2);
						String predicted;
						if(bimodel_count_bits[get_index_value] < mid) 
						{
							predicted = "n";
						}  
						else 
						{
							predicted = "t";
						}
						if(!income.equals(predicted)) 
						{
							miss_count = miss_count + 1;
						}
						if(income.equals("t"))
						{
							if(bimodel_count_bits[get_index_value] != high)
							{
								bimodel_count_bits[get_index_value] = bimodel_count_bits[get_index_value] + 1;
							}
						}
						else
						{
							if(bimodel_count_bits[get_index_value] != low)
							{
								bimodel_count_bits[get_index_value] = bimodel_count_bits[get_index_value] - 1;
							}
						}
					}
					myReader.close();
				} catch (FileNotFoundException e) 
				{
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				double val = (  (  (double)miss_count/(double)total_count    )  *10000.0);
				
				double missrate = Math.round(val)/100.0;
				System.out.println("OUTPUT");
				System.out.println("number of prediction      :" + total_count);
				System.out.println("number of misprediction   :" + miss_count);
				System.out.println("misprediction rate        :" + missrate + "%");
				for(int i = 0; i<bimodel_count_bits.length; i++) 
				{
					System.out.println(i + "   " + bimodel_count_bits[i]);
				}
			}
		}
		// incorrect parameters passing
		else 
		{
			System.out.println("something went wrong please try again");
		}
	}
}
