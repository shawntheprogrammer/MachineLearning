package HW2;

public class Controller {
	
	public static void main(String[] args) {
		// l k trainningset validationset testset
	    if(args.length == 0 || args.length != 6)
	    {
	        System.out.println("6 pass in parameters are needed!");
	        System.exit(0);
	    }
	    int l = Integer.valueOf(args[0].trim());
	    int k = Integer.valueOf(args[1].trim());
	    //"C:/Users/Shawn/Dropbox/Machine Learning/HWS/hw2/training_set.csv";
		String trainpath = args[2].trim();
		DataSet trainDataSet = new DataSet(trainpath);
		//"C:/Users/Shawn/Dropbox/Machine Learning/HWS/hw2/validation_set.csv";
		String validationpath = args[3].trim();
		DataSet validationDataSet = new DataSet(validationpath);
		//"C:/Users/Shawn/Dropbox/Machine Learning/HWS/hw2/test_set.csv";
		String testpath = args[3].trim();
		DataSet testDataSet = new DataSet(testpath);
		boolean print = (args[5].trim().toLowerCase().equals("yes")) ? true : false; 
		
		DecisionTree train_tree = new DecisionTree(trainDataSet);

		double ac1 = train_tree.getAccuracy(testDataSet);
		
		DecisionTree prune_tree = train_tree.postPrune(l, k, validationDataSet);
		double ac2 = prune_tree.getAccuracy(testDataSet);
		System.out.println("(l = " + l + ", k = " + k + ")");
		System.out.println("trained decision tree accuracy on test dataset: " + ac1);
		System.out.println( "post pruned tree accuracy on test dataset: " + ac2);
		if(print) {
			System.out.println();
			System.out.println("trained decision tree print as below");
			train_tree.print();
			System.out.println();
			System.out.println("post pruned decision tree print as below");
			prune_tree.print();
		}
	}
}
