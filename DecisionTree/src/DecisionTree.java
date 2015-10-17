package HW2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DecisionTree {
	public TreeNode root;
	public DecisionTree(){
		
	}
	public DecisionTree(DecisionTree tree) {
		DecisionTree dt = copyTree(tree);
		this.root = dt.root;
	}
	
	public DecisionTree(DataSet dataSet) {
		HashSet<Integer> attributeIndices = new HashSet<>();
		//indices  also the max level
		for(int i = 0; i < dataSet.attributeNames.size(); i++)
			attributeIndices.add(i);
		//indices of the entities in dataset, removes it 
		root = constructTree(dataSet.entities, attributeIndices);
	}
	
	//@para List<DataEntity>  data entities
	//@para HashSet<Integer>  indices of the attributes need to be considered, backtracking to avoid 
	//                        calculate all the entropy at each level
	//return TreeNode node
	//recursively build the tree, stops when there is no information gain or 
	public TreeNode constructTree(List<DataEntity> entities, HashSet<Integer> attributeIndices) {
		int[] count = countClass(entities);
		//do not split when run out of questions or 0 impurity
		//base case
		if(count[0] == 0 || count[1] == 0 || attributeIndices.size() == 0) {
			TreeNode node = new TreeNode(count[0], count[1]);
			return node;
		}
		
		//get maximum information gain split attribute column
		double entropy = getEntropy(count[0], count[1]);
		int maxGainAttributeCol = -1;
		double maxGain = (double)Integer.MIN_VALUE;
		for(Integer attributeCol : attributeIndices) {
			double subtreeEntropy = getSubtreeEntropy(entities, attributeCol);
			double gain = entropy - subtreeEntropy;
			if(gain > maxGain) {
				maxGain = gain;
				maxGainAttributeCol = attributeCol;
			}
		}
		if(maxGain < 0.001) {
			return new TreeNode(count[0], count[1]);
		}
		TreeNode node = new TreeNode(count[0], count[1], maxGainAttributeCol);
		//get the examples for left and right subtree
		List<List<DataEntity>> left_right = splitExamples(entities, maxGainAttributeCol); 
		List<DataEntity> left = left_right.get(0);
		List<DataEntity> right = left_right.get(1);
		
		//backtracking--removes the attributeCol of this node from indices
		attributeIndices.remove(maxGainAttributeCol);
		
		node.left = constructTree(left, attributeIndices);
		node.right = constructTree(right, attributeIndices);
		
		//add the attributeCol of this node back
		attributeIndices.add(maxGainAttributeCol);
		return node;
	}
	
	//traverse the data entities and split it based on entity.attributes.get(attributeCol) value
	//0 goes to the left 1 goes to the right
	private List<List<DataEntity>> splitExamples(List<DataEntity> entities, int attributeCol) {
		List<List<DataEntity>> left_right = new ArrayList<>();
		List<DataEntity> left = new ArrayList<>();
		List<DataEntity> right = new ArrayList<>();
		for(DataEntity entity : entities) {
			if(entity.attributes.get(attributeCol) == 0)
				left.add(entity);
			else
				right.add(entity);
		}
		left_right.add(left);
		left_right.add(right);
		return left_right;
	}

	private double getSubtreeEntropy(List<DataEntity> entities, int attributeCol) {
		List<List<DataEntity>> left_right = splitExamples(entities, attributeCol);
		List<DataEntity> left = left_right.get(0);
		List<DataEntity> right = left_right.get(1);
		int[] leftClassCount = countClass(left);
		int[] rightClassCount = countClass(right);
		double leftEntropy = getEntropy(leftClassCount[0], leftClassCount[1]);
		double rightEntropy = getEntropy(rightClassCount[0], rightClassCount[1]);
		double r1 = (double)left.size()/(double)(left.size() + right.size());
		double r2 = 1.0 - r1;
		double result = r1 * leftEntropy + r2 * rightEntropy;
		return result;
	}
	
	//count the num of class 0 and 1 in the examples
	public int[] countClass(List<DataEntity> entities) {
		int num0 = 0;
		int num1 = 0;
		for(DataEntity entity : entities) {
			if(entity.label == 0)
					num0++;
			else
					num1++;
		}
		return new int[]{num0, num1};
	}
	
	public double getEntropy(int num0, int num1) {
	  if(num0 == 0 || num1 == 0) {
		  return 0.0;
	  }
	  // calculate the entropy
	  double result = 0.0;
	  double f1 = (double)num0/(double)(num0+num1);
	  double f2 = 1.0 - f1;
	  result = - f1 * (Math.log(f1) / Math.log(2)) - f2 * (Math.log(f2) / Math.log(2));
	  return result;
	}

	public DecisionTree postPrune(int l, int k, DataSet validationDataSet) {
		DecisionTree thisTree = new DecisionTree();
		thisTree.root = this.root;
		DecisionTree bestTree = thisTree;
		double bestAccuracy = bestTree.getAccuracy(validationDataSet);
		for(int i = 1; i <= l; i++) {
			DecisionTree tree = new DecisionTree(thisTree);
			int m = (int)(Math.random()*k) + 1;
			//root might be chosen
			//break if it's the deal, out of bound
			for(int j = 1; j <= m; j++) {
				List<TreeNode> nonleaf = getNonleaf(tree);
				int n = nonleaf.size();
				if(n == 0)
					break;
				int p = (int)(Math.random() * n);
				makeItLeaf(nonleaf.get(p));
			}
			double accuracy = tree.getAccuracy(validationDataSet);
			if(accuracy > bestAccuracy) {
				bestTree = tree;
			}
		}
		return bestTree;
	}
	
	private TreeNode makeItLeaf(TreeNode treeNode) {
		int[] nums = getLeafNums(treeNode);
		treeNode.num0 = nums[0];
		treeNode.num1 = nums[1];
		if(nums[0] > nums[1])	{
			treeNode.label = 0;
		}else if(nums[1] > nums[0]) {
			treeNode.label = 1;
		}else{
			treeNode.label = (int)(Math.random()*2);
		}
		treeNode.isLeaf = true;
		return null;
	}
	
	private int[] getLeafNums(TreeNode node) {
		if(node.isLeaf) {
			return new int[]{node.num0, node.num1};
		}
		int[] res = new int[]{0, 0};
		int[] left = getLeafNums(node.left);
		int[] right = getLeafNums(node.right);
		res[0] = left[0] + right[0];
		res[1] = left[1] + right[1];
		return res;
	}
	//return a list of all the nonleaf node
	private List<TreeNode> getNonleaf(DecisionTree tree) {
		List<TreeNode> list = new ArrayList<>();
		getNonleaf(tree.root, list);
		return list;
	}
	private void getNonleaf(TreeNode node, List<TreeNode> list) {
		if(node == null || node.isLeaf) {
			return;
		}
		list.add(node);
		getNonleaf(node.left, list);
		getNonleaf(node.right, list);
		
	}
	public void print() {
		print(root, "");
	}
	
	public void print(TreeNode node, String s) {
		if(node.isLeaf) {
			return;
		}
		String left = s + DataSet.attributeNames.get(node.attributeCol) + " = 0 : ";
		String right = s + DataSet.attributeNames.get(node.attributeCol) + " = 1 : ";
		if(node.left.isLeaf) {
			left += node.left.getLabel();
		}
		if(node.right.isLeaf) {
			right += node.right.getLabel();
		}
		System.out.println(left);
		print(node.left, s + "| ");
		
		System.out.println(right);
		print(node.right, s + "| ");
	}
	
	public double getAccuracy(DataSet dataSet) {
		List<DataEntity> entities = dataSet.entities;
		//number of correctly classfied
		int num = 0;
		for(DataEntity entity : entities) {
			TreeNode cur = root;
			while(!cur.isLeaf) {
				int i = cur.attributeCol;
				int val = entity.getValue(i);
				if(val == 0) {
					cur = cur.left;
				}else{
					cur = cur.right;
				}
			}
			//cur is leafnode compare cur.getLabel() with entity.label
			if(cur.getLabel() == entity.label)
				num++;
		}
		double res = (double)num / (double)entities.size();
		return res;
	}

	public DecisionTree copyTree(DecisionTree tree) {
		TreeNode node = copyTree(tree.root);
		DecisionTree res = new DecisionTree();
		res.root = node;
		return res;
	}

	public TreeNode copyTree(TreeNode node) {
		if(node == null) {
			return null;
		}
		TreeNode newNode = new TreeNode(node);
		newNode.left = copyTree(node.left);
		newNode.right = copyTree(node.right);
		return newNode;
	}
	
	public class TreeNode{
		public TreeNode left;
		public TreeNode right;
		//the decision where to be made upon the value of dataEntity's attribute index
		public int attributeCol = -1;
		//treat every node as potential leaf node, get the number of examples,
		public int num0;
		public int num1;
		public int label;
		//attributes of leaf node
		public boolean isLeaf;
		
		public TreeNode(int num0, int num1) {
			this.num0 = num0;
			this.num1 = num1;
			this.isLeaf = true;
			if(num0 > num1)	{
				label = 0;
			}else if(num1 > num0) {
				label = 1;
			}else{
				label = (int)(Math.random()*2);
			}
		}
		
		public TreeNode(int num0, int num1, int attributeCol) {
			this.num0 = num0;
			this.num1 = num1;
			this.attributeCol = attributeCol;
			this.isLeaf = false;
		}
		
		public TreeNode(TreeNode node) {
			this.num0 = node.num0;
			this.num1 = node.num1;
			this.isLeaf = node.isLeaf;
			this.attributeCol = node.attributeCol;
			this.label = node.label;
		}

		//
		public int getLabel( ) {
			return this.label;
		}
	}
}
