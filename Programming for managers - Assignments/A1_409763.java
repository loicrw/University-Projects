package Assignment1_package;

public class A1_409763 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//1+2 - choose three numbers and initialise them
		double a = 1;
		double b = 1;
		double c = 1;
		
		//3 - compute the average
		double meanValue = (a+b+c)/3;
		
		//4 - compute the median
		double medianValue = 0;
		
		if ((a <=b && a >= c) || (a <= c && a >= b)){
			medianValue = a;
		} else if (( b <= a && b >= c) || (b <= c && b >= a)){
			medianValue = b;
		} else {
			medianValue = c;
		}
		
		//4 bonus - compute the median with no logical operators
	    if((a-b)*(b-c) > 0) {
	    	medianValue = b;
	    } else if((a-b)*(a-c) > 0) {
	    	medianValue = c;
	    } else {
	    	medianValue = a;
	    }
		
		//5 - print the results
		System.out.println("The three input variables are " + a + ", "+ b + ", and " + c);
		System.out.println("**************************************************************");
		System.out.println("The median is " + medianValue);
		System.out.println("The average is " + meanValue);
		System.out.println("**************************************************************");
		
		//5d - compare mean and median
		if (medianValue == meanValue){
			System.out.println("The median and the average are equal");
		} else if (medianValue > meanValue) {
			System.out.println("The median is larger than the average");
		} else {
			System.out.println("The median is smaller than the average");
		}
				
//--------------------------------------------------
	}

}
