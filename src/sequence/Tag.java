package sequence;

public class Tag {

/*	B_PROB ("B-problem"),
	I_PROB ("I-problem"),
	O_PROB ("O-problem"),
	B_TREAT ("B-treatment"),
	I_TREAT ("I-treatment"),
	O_TREAT ("O-treatment"),
	B_TEST ("B-test"),
	O_TEST ("I-test"),
	I_TEST ("O-test"),
	O ("O");
*/
    public final String label;
    public final String BIO;
    public final String classLab;
    
    public Tag(String label) {
        this.label = label;
        BIO = ""+this.label.charAt(0);
        if (BIO.equals("O")) classLab = "O";
        else classLab = this.label.substring(2);        
    }
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}


