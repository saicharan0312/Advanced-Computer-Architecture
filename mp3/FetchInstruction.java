import java.util.*;

public class FetchInstruction extends sim {

    ArrayList<Integer> in_cycle_number = new ArrayList<Integer>();
    ArrayList<Integer> in_cycle_stay = new ArrayList<Integer>();

    int in_operation_type;
    int in_destionation_register;
    int in_source_register_1;
    int in_source_register_2;
    int in_instruction_execution_latency;
    int in_s1_dependent_on;
    int in_s2_dependent_on;
    int in_cwicgi;

    String in_message;
    String in_s1_state;
    String in_s2_state;
    String in_instruction_state;

    public FetchInstruction(String incoming_instruction) {
        String[] data = incoming_instruction.split(" ");
        this.in_message = data[0];
        this.in_operation_type = Integer.parseInt(data[1]);
        this.in_destionation_register = Integer.parseInt(data[2]);
        this.in_source_register_1 = Integer.parseInt(data[3]);
        this.in_source_register_2 = Integer.parseInt(data[4]);
        this.in_s1_dependent_on = -1;
        this.in_s2_dependent_on = -1;
    }
    public void in_update_cycle_number(int in_c_n) {
        this.in_cycle_number.add(in_c_n);
    }
    public void in_update_instruction_execution_latency() {
        if(this.in_operation_type == 0) {
            this.in_instruction_execution_latency = 1;
            this.in_cwicgi = this.in_cycle_number.get(3) + in_instruction_execution_latency;
        } else if(this.in_operation_type == 1) {
            this.in_instruction_execution_latency = 2;
            this.in_cwicgi = this.in_cycle_number.get(3) + in_instruction_execution_latency;
        } else if(this.in_operation_type == 2) {
            this.in_instruction_execution_latency = 5;
            this.in_cwicgi = this.in_cycle_number.get(3) + in_instruction_execution_latency;
        }
    }
}