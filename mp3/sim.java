import java.io.*;
import java.util.*;
import java.lang.Math;

class sim {
    static int tcn;
    static int[] register_tag_index = new int[128];
	static String[] register_hex_tag = new String[128];
    static ArrayList<Integer> used_list  = new ArrayList<Integer>();
    static int tag = 0;
	public static void main(String[] args) throws Exception {
		if(args.length==3) {
			int N = Integer.parseInt(args[0]);
			int S = Integer.parseInt(args[1]);
			String test_file = args[2];
			Stack in_reverse_order = new Stack();
			Stack stack_order = new Stack();
			Scanner myReader = null;

			File test_file_copy = new File(test_file);

            int total_instructions_count = 0;

            FetchInstruction ofei = null;
            HashMap<Integer, FetchInstruction> Instruction_details = new HashMap<Integer, FetchInstruction>();

            //Issue 
            ArrayList<Integer> is_execute_list = new ArrayList<Integer>();

            //Instruction
            // int in_operation_type = 0;
            // String in_message = "";
            // int in_destionation_register = 0;
            // int in_source_register_1 = 0;
            // int in_source_register_2 = 0;
            // String[] in_contents;
            // String in_instruction_state = "";
            // String in_s1_state = "";
            // String in_s2_state = "";
            // int in_instruction_execution_latency = 0;
            // int in_s1_dependent_on = 0;
            // int in_s2_dependent_on = 0;
            // int in_cycle_to_execute = 0;
            // ArrayList<Integer> in_cycle_number = new ArrayList<Integer>();
            // ArrayList<Integer> in_cycle_stay = new ArrayList<Integer>();

            //Fetch
            int f_size = 0;
            Stack f_stack; 
            int f_dispatch_counter = 0;
            ArrayList<Integer> f_dispatch_list = new ArrayList<Integer>();

            //Dispatch
            int d_schedule_counter = 0;
            int d_avalible_size = 0;
            ArrayList<Integer> d_issue_list = new ArrayList<Integer>();  

			try {
				myReader = new Scanner(test_file_copy);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				in_reverse_order.push(data);
				total_instructions_count = total_instructions_count + 1;
			}
			while(!in_reverse_order.isEmpty()) {
				String in_data = (String) in_reverse_order.pop();
				stack_order.push(in_data);
			}
            f_stack = stack_order;
			Arrays.fill(register_tag_index, -1);
			Arrays.fill(register_hex_tag, "R");
			boolean isTrue = true;
			do {
                // execute operation
                ArrayList<Integer> tExecuteList = new ArrayList<Integer>();
                for(int i = 0; i< is_execute_list.size();i++) {
                    tExecuteList.add(is_execute_list.get(i));
                }
                int exe_size = is_execute_list.size();
                for(int i=0;i<exe_size;i++) {
                    int wviitr,ttn;
                    // FetchInstruction ofei = null;
                    ttn = is_execute_list.get(i);
                    ofei = Instruction_details.get(ttn);
                    if(ofei.in_cwicgi == tcn) {
                        tExecuteList.remove((Object)(ttn));
                        used_list.add(ttn);
                        ofei.in_instruction_state = "WB";
                        ofei.in_update_cycle_number(tcn);
                        if(ofei.in_destionation_register != (-1)) {
                            wviitr = register_tag_index[ofei.in_destionation_register];
                            if(ttn == wviitr) {
                                register_hex_tag[ofei.in_destionation_register] = "R";
                            }
                        }
                        for(int is = 0; is < d_issue_list.size(); is++) {
                            int itwp;
                            FetchInstruction ins;
                            itwp = d_issue_list.get(is);
                            ins = Instruction_details.get(itwp);
                            if(ins.in_s1_dependent_on == ttn) {
                                ins.in_s1_state = "R";
                            }
                            if(ins.in_s2_dependent_on == ttn) {
                                ins.in_s2_state = "R";
                            }
                        }
                        for(int is = 0; is < f_dispatch_list.size(); is++) {
                            int itwp;
                            FetchInstruction ins;
                            //System.out.println("i="+is);
                            itwp = f_dispatch_list.get(is);
                            ins = Instruction_details.get(itwp);
                            if(ins.in_instruction_state.contentEquals("ID")) {
                                if(ins.in_s1_dependent_on == ttn) {
                                    ins.in_s1_state = "R";
                                }
                                if(ins.in_s2_dependent_on == ttn) {
                                    ins.in_s2_state = "R";
                                }
                            }
                        }
                    }
                }
                is_execute_list.clear();
                for(int i=0; i<tExecuteList.size();i++) {
                    is_execute_list.add(tExecuteList.get(i));
                }
                // end execute operation

                // issue operation
                int flv;
                int fflv;
                ArrayList<Integer> ltir = new ArrayList<Integer>();
                int copv = 0;
                int ilv;
                for(int i = 0;i<d_issue_list.size(); i++) {
                    ilv = d_issue_list.get(i); 
                    ofei = Instruction_details.get(ilv);
                    if(ofei.in_s1_state.contentEquals("R") && ofei.in_s2_state.contentEquals("R")) {
                        copv = copv + 1;
                    }
                }
                flv = copv;
                fflv = 0;
                if(flv<=N+1) {
                    fflv = flv;
                } else if(flv>N+1) {
                    fflv = N+1;
                }
                for(int i = 0; i<d_issue_list.size();i++) {
                    ilv = d_issue_list.get(i);
                    ofei = Instruction_details.get(ilv);
                    if(ofei.in_s1_state.contentEquals("R") && ofei.in_s2_state.contentEquals("R")) {
                        ltir.add(ilv);
                    }
                }
                for(int i = 0;i<fflv ; i++) {
                    int eititwmtal;
                    eititwmtal = ltir.get(i);
                    d_issue_list.remove((Object)(eititwmtal));
                    d_schedule_counter = d_schedule_counter - 1;
                    is_execute_list.add(eititwmtal);
                    ofei = Instruction_details.get(eititwmtal);
                    ofei.in_instruction_state = "EX";
                    ofei.in_update_cycle_number(tcn);
                    ofei.in_update_instruction_execution_latency();
                }
                // end issue operation

                //dispatch operation
                int az;
                int eitd;

                int d_fls = 0;
                int in_f_size = f_dispatch_list.size();
                int fsisc = S-d_schedule_counter;
                if(in_f_size>=fsisc) {
                    d_fls = fsisc;
                } else if(in_f_size < fsisc) {
                    d_fls = in_f_size;
                }
                az = d_fls;
                for(int i=0;i<az;i++) {
                    FetchInstruction obj = null;
                    eitd = f_dispatch_list.get(0);
                    obj = Instruction_details.get(eitd);
                    if(obj.in_instruction_state.contentEquals("ID")) {
                        d_issue_list.add(eitd);
                        d_schedule_counter = d_schedule_counter + 1;
                        f_dispatch_list.remove(0);
                        f_dispatch_counter = f_dispatch_counter - 1;
                        obj.in_instruction_state = "IS";
                        obj.in_cycle_number.add(tcn);
                    }
                }
                for(int i=0;i<f_dispatch_list.size();i++) {
                    int tvidl = f_dispatch_list.get(i);
                    FetchInstruction obj = null;
                    obj = Instruction_details.get(tvidl);
                    if(obj.in_instruction_state.contentEquals("IF")) {
                        String s1 = null;
                        String s2 = null;
                        obj.in_instruction_state = "ID";
                        if(obj.in_source_register_1 == -1) {
                            s1 = "R";
                        } else if(register_hex_tag[obj.in_source_register_1].contentEquals("R")) {
                            s1 = register_hex_tag[obj.in_source_register_1];
                        } else if(register_hex_tag[obj.in_source_register_1].contentEquals("N")) {
                            s1 = register_hex_tag[obj.in_source_register_1];
                            obj.in_s1_dependent_on = register_tag_index[obj.in_source_register_1];
                        }
                        obj.in_s1_state = s1;
                        if(obj.in_source_register_2 == -1) {
                            s2 = "R";
                        } else if(register_hex_tag[obj.in_source_register_2].contentEquals("R")) {
                            s2 = register_hex_tag[obj.in_source_register_2];
                        } else if(register_hex_tag[obj.in_source_register_2].contentEquals("N")) {
                            s2 = register_hex_tag[obj.in_source_register_2];
                            obj.in_s2_dependent_on = register_tag_index[obj.in_source_register_2];
                        }
                        obj.in_s2_state = s2;
                        if(obj.in_destionation_register != (-1)) {
                            register_tag_index[obj.in_destionation_register] = tvidl;
                            register_hex_tag[obj.in_destionation_register] = "N";
                        }
                        obj.in_cycle_number.add(tcn);
                    }
                }
                //end dispatch operation

                // fetch operatiom
                int ss;
                int flcboacs;
                if(f_stack.size() != 0) {
                    ss = f_stack.size();
                    int ads;
                    int in_size = 0;
                    int fls = 0;
                    if(ss>=N) {
                        in_size = N;
                    } else if(ss<N) {
                        in_size = ss;
                    }
                    ads = (2*N) - f_dispatch_counter;
                    if(ads == 0) {
                        fls = 0;
                    } else if(ads >= in_size) {
                        fls = in_size;
                    } else if(ads<in_size) {
                        fls = ads;
                    }
                    flcboacs = fls;
                    for(int i=0;i<flcboacs;i++) {
                        String ci = "";
                        ci = (String) f_stack.pop();
                        ofei = new FetchInstruction(ci);
                        ofei.in_cycle_number.add(tcn);
                        Instruction_details.put(tag, ofei);
                        ofei.in_instruction_state = "IF";
                        f_dispatch_list.add(tag);
                        f_dispatch_counter = f_dispatch_counter + 1;
                        tag = tag + 1;

                    }
                }
                //end fetch operation

                //advance cycle isTrue
                tcn = tcn + 1;
                if(
                    f_stack.isEmpty() && 
                    f_dispatch_list.isEmpty() &&
                    is_execute_list.isEmpty() &&
                    d_issue_list.isEmpty()
                ) {
                    isTrue = false;
                } else {
                    isTrue = true;
                }
                //end advanve cycle
              
            } while(isTrue);
            for(int i=0;i<Instruction_details.size();i++) {
                FetchInstruction obj = null;
                obj = Instruction_details.get(i);
                int Instruction_Fetch_Latency = obj.in_cycle_number.get(1) - obj.in_cycle_number.get(0);
                int Instruction_Decode_Latency = obj.in_cycle_number.get(2) - obj.in_cycle_number.get(1);
                int Instruction_Memory_Latency = obj.in_cycle_number.get(3) - obj.in_cycle_number.get(2);
                int Instruction_Execute_Latency = obj.in_cycle_number.get(4) - obj.in_cycle_number.get(3);
                System.out.println(
                    i+" "+
                    "fu{"+obj.in_operation_type+"}"+
                    "src{"+obj.in_source_register_1+","+obj.in_source_register_2+"}"+
                    "dst{"+obj.in_destionation_register+"}"+
                    "IF{"+obj.in_cycle_number.get(0)+","+Instruction_Fetch_Latency+"}"+
                    "ID{"+obj.in_cycle_number.get(1)+","+Instruction_Decode_Latency+"}"+
                    "IS{"+obj.in_cycle_number.get(2)+","+Instruction_Memory_Latency+"}"+
                    "EX{"+obj.in_cycle_number.get(3)+","+Instruction_Execute_Latency+"}"+
                    "WB{"+obj.in_cycle_number.get(4)+","+"1"+"}"
                );
            }
            double IPC_Value;
            IPC_Value = (double) total_instructions_count / (double) tcn;
            System.out.println("number of instructions = " + total_instructions_count);
            System.out.println("number of cycles = " + tcn);
            System.out.println("IPC = " + IPC_Value);
		}
		else {
			System.out.println("Something went wrong");
		}
}
}