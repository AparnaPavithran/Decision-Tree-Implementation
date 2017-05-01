import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class id3 {
	
	public static int tree_count=0;
	public static String[] tree=new String[100000000];

	public static void main(String[] args) throws IOException {

		int L,K;
		String training,validation_set,test_s,prin;
		
		//input arguments
		if(args.length>0)
		{
			L=Integer.parseInt(args[0]);
			K=Integer.parseInt(args[1]);
			training=args[2];
			validation_set=args[3];
			test_s=args[4];
			prin=args[5];
		}
		else  //default arguments if input string is missing
		{
			L=5;
			K=4;
			training="training_set.csv";
			validation_set="validation_set.csv";
			test_s="test_set.csv";
			prin="yes";
		}
		
		String splitBy = ",",in_attr;
		String[] b;
		int i=0,j=0,size1=0,size2=0;

		BufferedReader csvReader= new BufferedReader(new FileReader(training));		
		
		while((in_attr=csvReader.readLine()) != null){
				b = in_attr.split(splitBy);
				size2=b.length;
				i++;
		}
		size1=i;
		
		//how to get input into a matrix
		BufferedReader csvReader_in= new BufferedReader(new FileReader(training));
		String[][] attribute=new String[size1][size2];
		i=0;j=0;
		while((in_attr=csvReader_in.readLine()) != null){
			b = in_attr.split(splitBy);
			size2=b.length;
			for(j=0;j<size2;j++){
			attribute[i][j]=b[j];
			}
			i++;
		}
        csvReader.close();
        csvReader_in.close();
        
        i=0;j=0;size1=0;size2=0;
        BufferedReader csvReader_val= new BufferedReader(new FileReader(validation_set));		
		
		while((in_attr=csvReader_val.readLine()) != null){
				b = in_attr.split(splitBy);
				size2=b.length;
				i++;
		}
		size1=i;
		
		//how to get input into a matrix
		BufferedReader csvReader_val_in= new BufferedReader(new FileReader(validation_set));
		String[][] validation=new String[size1][size2];
		i=0;j=0;
		while((in_attr=csvReader_val_in.readLine()) != null){
			b = in_attr.split(splitBy);
			size2=b.length;
			for(j=0;j<size2;j++){
				validation[i][j]=b[j];
			}
			i++;
		}
		csvReader_val.close();
		csvReader_val_in.close();
		
		i=0;j=0;size1=0;size2=0;
		BufferedReader csvReader_test= new BufferedReader(new FileReader(test_s));		
		
		while((in_attr=csvReader_test.readLine()) != null){
				b = in_attr.split(splitBy);
				size2=b.length;
				i++;
		}
		size1=i;
		
		//how to get input into a matrix
		BufferedReader csvReader_test_in= new BufferedReader(new FileReader(test_s));
		String[][] test_set=new String[size1][size2];
		i=0;j=0;
		while((in_attr=csvReader_test_in.readLine()) != null){
			b = in_attr.split(splitBy);
			size2=b.length;
			for(j=0;j<size2;j++){
				test_set[i][j]=b[j];
			}
			i++;
		}
		csvReader_test.close();
		csvReader_test_in.close();
        
        
		
        i=id3_calc(attribute);
        
       
        if(prin.contentEquals("yes"))
        {
        	System.out.println();
        	 System.out.println("ID3 (Heuristic 1) TREE :");
        	 System.out.println();
        	print_tree(tree,tree_count);
        	System.out.println();
        }
        
        double ac,t;
		ac=accuracy(test_set,tree,tree_count);
		t=validation.length-1;
		System.out.println();
		System.out.println("Matched class variables : "+ac+" out of : "+t);
		ac=ac/t*100;
		System.out.println();
		System.out.println("Accuracy of ID3(Heuristic1) Tree : "+ac);
		System.out.println();
		String[] s=pruning(validation,tree,L,K);
		
		if(prin.contentEquals("yes"))
        {
			System.out.println();
			System.out.println("PRUNED TREE FOR ID3 (Heuristic 1):");
			System.out.println();
        	print_tree(s,s.length);
        	System.out.println();
        }
        tree_count=0;
        i=VI_calc(attribute);
        
        if(prin.contentEquals("yes"))
        {
        	System.out.println();
        	System.out.println("VI (Heuristic 2) TREE :");
        	System.out.println();
        	print_tree(tree,tree_count);
        	System.out.println();
        }

        ac=accuracy(test_set,tree,tree_count);
		t=validation.length-1;
		System.out.println();
		System.out.println("Matched class variables : "+ac+" out of : "+t);
		ac=ac/t*100;
		System.out.println();
		System.out.println("Accuracy of VI(Heuristic2) Tree : "+ac);
		System.out.println();
		
		pruning(validation,tree,L,K);
		if(prin.contentEquals("yes"))
        {
			System.out.println();
			System.out.println("PRUNED TREE  FOR VI (Heuristic 2):");
			System.out.println();
        	print_tree(s,s.length);
        	System.out.println();
        }
		
	}
	
	private static void print_tree(String[] tree,int tree_count) {
		int i=0,bracket=1;
		for(i=1;i<tree_count;i++)
		{
			if(tree[i].contentEquals("("))
			{
				bracket++;
				for(int j=1;j<bracket;j++)
				{
					System.out.print("| ");
				}
			}
			else if(tree[i].contentEquals(")"))
			{
				bracket--;
				for(int j=1;j<bracket;j++)
				{
					System.out.print("| ");
				}
			}
			else
			{
				for(int j=1;j<bracket;j++)
				{
					System.out.print("| ");
				}
				System.out.print(tree[i]);
				System.out.println();
			}
			
		}
	}
	
	private static int VI_calc(String[][] attribute) {
		
		//decide root node with 
		int i=0,j=0,size2=0;
		size2=attribute[0].length;
		String sort_v="no output";
		
		//gain
		String[] gain=new String[size2-1];
		double[] gain_v=new double[size2-1];
		int k;
		double value,entropy_pos,entropy_neg;
		
		if(attribute[0].length>=2)
		{

		for(k=0;k<attribute[0].length-1;k++) 
		{
			gain[k]=attribute[0][k];
		}
		double K0=0.0,K1=0.0,K;
		K=attribute.length-1;
		for(i=1;i<attribute.length;i++)
		{
			K1=K1+Double.parseDouble(attribute[i][(attribute[0].length-1)]);
		}
		K0=K-K1;
		value=(K0*K1)/(K*K);
		
		for(k=0;k<attribute[0].length-1;k++) 
		{	
			String[][] attr=new String[attribute.length][2];
			
			int len=attribute[0].length-1;
			
			for(i=0;i<attribute.length;i++)   
			{
				attr[i][0]=attribute[i][k];
				attr[i][1]=attribute[i][len];
			}
			
			int count=0;
			
			for(i=1;i<attr.length;i++)
			{
				count=count+Integer.parseInt(attr[i][0]);
			}
			
			String[][] pos=new String[count+1][2];
			String[][] neg=new String[attr.length-count][2];
			i=0;
			pos[i][0]=attr[i][0];
			neg[i][0]=attr[i][0];
			pos[i][1]=attr[i][1];
			neg[i][1]=attr[i][1];
			int m=1,n=1;
			for(i=1;i<attr.length;i++)
			{
				if(Integer.parseInt(attr[i][0])==1)
				{
					pos[m][0]=attr[i][0];
					pos[m][1]=attr[i][1];
					m++;
				}
				else if(Integer.parseInt(attr[i][0])==0)
				{
					neg[n][0]=attr[i][0];
					neg[n][1]=attr[i][1];
					n++;
				}
				
			}
		
			double pos_K0=0.0,pos_K1=0.0,pos_K;
			pos_K=pos.length-1;
			for(i=1;i<pos.length;i++)
			{
				pos_K1=pos_K1+Double.parseDouble(pos[i][(pos[0].length-1)]);
			}
			pos_K0=pos_K-pos_K1;
			entropy_pos=(pos_K0*pos_K1)/(pos_K*pos_K);
			
			double neg_K0=0.0,neg_K1=0.0,neg_K;
			neg_K=neg.length-1;
			for(i=1;i<neg.length;i++)
			{
				neg_K1=neg_K1+Double.parseDouble(neg[i][(neg[0].length-1)]);
			}
			neg_K0=neg_K-neg_K1;
			entropy_neg=(neg_K0*neg_K1)/(neg_K*neg_K);
			
			double c,attrlen;
			c=count;
			attrlen=attr.length;
			gain_v[k]=value-(c/(attrlen-1))*entropy_pos-((attrlen-c)/(attrlen-1))*entropy_neg;
			
		}
		
		sort_v=sort(gain,gain_v);
		i=sort_v.indexOf('=');
		sort_v=sort_v.substring(i+1, sort_v.length());
		
		//next iteration
		
		i=0;
		j=0;
		int flag=0;
		int count=0,v=0;
		
		for(i=0;i<attribute[0].length;i++)
		{
			if(attribute[0][i].contentEquals(sort_v))
			{
				v=i;
				for(j=0;j<attribute.length;j++)
				{
					if(attribute[j][i].contentEquals("1"))
					count++;
				}
				break;
			}
			
		}
		
		int tot=0,tot1=0;
		int pos=0,neg=0,pos1=0,neg1=0;
		int len=attribute[0].length-1;

		int m=0,n=0;
		
		String[][] attribute1=new String[count+1][attribute[0].length-1];
		String[][] attribute2=new String[attribute.length-count][attribute[0].length-1];
		
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("1") && attribute[i][v].contentEquals("1"))
			{
				pos++;
				tot++;
			}
			else
				tot++;
		}
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("0") && attribute[i][v].contentEquals("1"))
			{
				pos1++;
				tot1++;
			}
			else
				tot1++;
		}
		
		if(tot==pos)
		{
			tree[tree_count]=sort_v+" 1 : 1";
			tree_count++;
			return 1;
		}
		else if(tot1==pos1)
		{
			tree[tree_count]=sort_v+" 1 : 0";
			tree_count++;
			return 1;
		}
		else 
		{	
			for(i=0;i<attribute[0].length;i++)
			{
				if(attribute[0][i].contentEquals(sort_v))
				{
					
				}
				else
				{
					attribute1[0][m]=attribute[0][i];
					m++;
				}
			}
			
			
			m=1;
			for(i=1;i<attribute.length;i++)
			{
				n=0;flag=0;
				for(j=0;j<attribute[0].length;j++)
				{
					if(j!=v)
					{
						if(attribute[i][v].contentEquals("1"))
						{
							flag=1;
							attribute1[m][n]=attribute[i][j];
							n++;
							
						}
				}
			}
				if(flag==1)
				{
					m++;
				}
			}
			
			tree[tree_count]="(";
			tree_count++;
			tree[tree_count]=sort_v+" 1 : ";
			tree_count++;
			i=VI_calc(attribute1);
			tree[tree_count]=")";
			tree_count++;
			
			
		}
		tot=0;
		tot1=0;
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("1") && attribute[i][v].contentEquals("0"))
			{
				neg++;
				tot++;
			}
			else
				tot++;
		}
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("0") && attribute[i][v].contentEquals("0"))
			{
				neg1++;
				tot1++;
			}
			else
				tot1++;
		}
		
		if(tot==neg)
		{
			tree[tree_count]=sort_v+" 0 : 1";
			tree_count++;
			return 1;
		}
		else if(tot1==neg1)
		{
			tree[tree_count]=sort_v+" 0 : 0";
			tree_count++;
			return 1;
		}
		else 
		{
			m=0;
			for(i=0;i<attribute[0].length;i++)
			{
				if(attribute[0][i].contentEquals(sort_v))
				{
					
				}
				else
				{
					attribute2[0][m]=attribute[0][i];
					m++;
				}
			}
			m=1;
			for(i=1;i<attribute.length;i++)
			{
				n=0;flag=0;
				for(j=0;j<attribute[0].length;j++)
				{
					if(j!=v)
					{
					if(attribute[i][v].contentEquals("0"))
					{
						flag=1;
						attribute2[m][n]=attribute[i][j];
						n++;
						
					}
					}
				}
				if(flag==1)
				{
					m++;
				}
			}
			
			tree[tree_count]="(";
			tree_count++;
			tree[tree_count]=sort_v+" 0 : ";
			tree_count++;
			i=VI_calc(attribute2);
			tree[tree_count]=")";
			tree_count++;
		}
		

		}
		return 1;
	}
	
	private static String[] pruning(String[][] validation,String[] tree_D,int l, int k) throws IOException
	{
		int i,j,dif=0,M;
		int leaf_node_count=0;
		int d_best_lenth=tree_count;
		double ac=0.0,ac1=0.0;
		
		String[] tree_D_best=new String[tree_count];
		String[] tree_D_new=new String[tree_count];
	
		for(i=0;i<d_best_lenth;i++)
		{
			tree_D_best[i]=tree_D[i];
		}
		
		int d_new_length=d_best_lenth;
		
		//start L
		for(int p=1;p<l;p++)
		{  /////////////L
			
			//replicate d new here from d best
			for(i=0;i<d_best_lenth;i++)
			{
				tree_D_new[i]=tree_D_best[i];
			}
			d_new_length=d_best_lenth;
			dif=0;
			Random r = new Random();
			M = r.nextInt(k-1) + 1;
			
			for(int q=1;q<M;q++)
			{	
				leaf_node_count=0;
				d_new_length=d_new_length-dif;
				for(i=0;i<d_new_length;i++)
				{
					if(tree_D_new[i].length()==7)
					{
						leaf_node_count++;
					}
				}
				
				int[] leaf_node=new int[leaf_node_count];
				j=0;
				for(i=0;i<d_new_length;i++)
				{
					if(tree_D_new[i].length()==7) 
					{
						leaf_node[j]=i;
						j++;
					}
				}
				
				int random=getRandom_number(1,d_new_length,leaf_node);
				//traverse tree from random and delete a sub tree
				int bracket=1;i=random;
				while(bracket!=0)
				{
					if(tree_D_new[i].contentEquals("("))
					{
						bracket++;
					}
					else if(tree_D_new[i].contentEquals(")"))
					{
						bracket--;
					}
					i++;
				}
				
				double neg=0.0,pos=0.0;
				// sub tree is from random to i
				for(j=random;j<i;j++)
				{
					if(tree_D_new[j].length()==8)   
					{
						if(tree_D_new[j].substring(7, 8).contentEquals("1"))
						{
							pos++;
						}
						else if(tree_D_new[j].substring(7, 8).contentEquals("0"))
						{
							neg++;
						}
					}
				}
				
				double total=pos+neg;
				double pos1,neg1;
				pos1=pos/total*100;
				neg1=neg/total*100;
				dif=i-random-1;
				if(pos1>=60)
				{
					//replace entire tree from random to i by behind tale of the tree
					tree_D_new[random]=tree_D_new[random].substring(0, 6)+" 1";
					tree_D_new[random+1]=")";
					
					if(random+2 != i)
					{
						for(j=random+2;j+dif<d_new_length;j++)
						{
							tree_D_new[j]=tree_D_new[j+dif];
						}
					}
					
				}
				else if(neg1>=60){
					//replace entire tree from random to i by behind tale of the tree
					tree_D_new[random]=tree_D_new[random].substring(0, 6)+" 0";
					tree_D_new[random+1]=")";
					if(random+2 != i)
					{
						for(j=random+2;j+dif<d_new_length;j++)
						{
							tree_D_new[j]=tree_D_new[j+dif];
						}
					}
				}
				
			}// repeat it for M times
			
			ac=accuracy(validation,tree_D_new,(d_new_length-dif));
			double t=validation.length-1;
			ac=ac/t*100;
			
			ac1=accuracy(validation,tree_D_best,tree_D_best.length);
			ac1=ac1/t*100;
			
			if(ac>ac1)
			{
				
				for(int x=0;x<d_new_length;x++)
				{
					tree_D_best[x]=tree_D_new[x];
				}
				d_best_lenth=d_new_length;
			}
			
		} /////////////L
		//// after re sizing array call accuracy fun n compare
		
			System.out.println();
			System.out.println("Accuracy of Pruned tree : "+ac);
			System.out.println();
		
		return tree_D_new;
	}
		
	private static int getRandom_number(int low, int high,int[]  leaf_node)
	{
		int flag=0;
		int Result=0;
		Random r = new Random();
		while(flag!=1)
		{
			Result = r.nextInt(high-low) + low;
			for(int i=0;i<leaf_node.length;i++)
			{
				if(Result==leaf_node[i])
				{
					flag=1;
					break;
				}
			}
		}
		
		
		return Result;
	}
	
	private static double accuracy(String[][] validation_set,String[] tree,int tree_count) throws IOException {
		//accuracy calculation
		String var,var1;
		int i,j=0,k=1,len;
		double match=0.0;
		
		len=validation_set[0].length-1;
        
		for(i=1;i<validation_set.length;i++)
		{
			
			k=1;
			var=tree[k];
			var = var.substring(0, 2);
			for(j=0;j<validation_set[0].length;j++)
			{
				if(validation_set[0][j].contentEquals(var))
				{
					break;
				}
			}
			for(k=0;k<tree_count;k++)
			{
				
				var1=var+" "+validation_set[i][j]+" : ";
				
				if(tree[k].contentEquals(var1) || tree[k].contentEquals(var1+"0") || tree[k].contentEquals(var1+"1"))
				{
					if(tree[k].contentEquals(var1+"0"))
					{ 
						if(validation_set[i][len].contentEquals("0"))
						{
							match=match+1.0;
							break;
						}
					}
					else if(tree[k].contentEquals(var1+"1"))
					{
						if(validation_set[i][len].contentEquals("1"))
						{
							match=match+1.0;
							break;
						}
					}
					else
					{
						for(int l=k+1;l<tree_count;l++)
						{
							
							if(tree[l].contentEquals("(") || tree[l].contentEquals(")"))
							{
							}
							else
							{
								var=tree[l];
								break;
							}
						}
						var = var.substring(0, 2);
						
						for(j=0;j<validation_set[0].length;j++)
						{
							if(validation_set[0][j].contentEquals(var))
							{
								break;
							}
						}
					}
			}
		}
			
		}
		return match;
		
	}
		
	
	private static int id3_calc(String[][] attribute) {
	
		//decide root node with 
		int i=0,j=0,size2=0;
		//size1=attribute.length;
		size2=attribute[0].length;
		String sort_v="no output";
		
		//gain
		String[] gain=new String[size2-1];
		double[] gain_v=new double[size2-1];
		int k;
		double value,entropy_pos,entropy_neg;
		
		if(attribute[0].length>=2)
		{

		for(k=0;k<attribute[0].length-1;k++) 
		{
			gain[k]=attribute[0][k];
		}
	
		value=entropy(attribute,attribute.length,attribute[0].length);
		
		for(k=0;k<attribute[0].length-1;k++) 
		{	
			String[][] attr=new String[attribute.length][2];
			
			int len=attribute[0].length-1;
			
			for(i=0;i<attribute.length;i++)   
			{
				attr[i][0]=attribute[i][k];
				attr[i][1]=attribute[i][len];
			}
			
			int count=0;
			
			for(i=1;i<attr.length;i++)
			{
				count=count+Integer.parseInt(attr[i][0]);
				
			}
			
			String[][] pos=new String[count+1][2];
			String[][] neg=new String[attr.length-count][2];
			i=0;
			pos[i][0]=attr[i][0];
			neg[i][0]=attr[i][0];
			pos[i][1]=attr[i][1];
			neg[i][1]=attr[i][1];
			int m=1,n=1;
			for(i=1;i<attr.length;i++)
			{
				if(Integer.parseInt(attr[i][0])==1)
				{
					pos[m][0]=attr[i][0];
					pos[m][1]=attr[i][1];
					m++;
				}
				else if(Integer.parseInt(attr[i][0])==0)
				{
					neg[n][0]=attr[i][0];
					neg[n][1]=attr[i][1];
					n++;
				}
				
			}
		
			
			entropy_pos=entropy(pos,pos.length,pos[0].length);
			entropy_neg=entropy(neg,neg.length,neg[0].length);
			double c,attrlen;
			c=count;
			attrlen=attr.length;
			gain_v[k]=value-(c/(attrlen-1))*entropy_pos-((attrlen-c)/(attrlen-1))*entropy_neg;
			
		}
		
		sort_v=sort(gain,gain_v);
		i=sort_v.indexOf('=');
		sort_v=sort_v.substring(i+1, sort_v.length());
		
		//next iteration
		
		i=0;
		j=0;
		int flag=0;
		int count=0,v=0;
		
		for(i=0;i<attribute[0].length;i++)
		{
			if(attribute[0][i].contentEquals(sort_v))
			{
				v=i;
				for(j=0;j<attribute.length;j++)
				{
					if(attribute[j][i].contentEquals("1"))
					count++;
				}
				break;
			}
			
		}
		
		int tot=0,tot1=0;
		int pos=0,neg=0,pos1=0,neg1=0;
		int len=attribute[0].length-1;

		int m=0,n=0;
		
		String[][] attribute1=new String[count+1][attribute[0].length-1];
		String[][] attribute2=new String[attribute.length-count][attribute[0].length-1];
		
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("1") && attribute[i][v].contentEquals("1"))
			{
				pos++;
				tot++;
			}
			else
				tot++;
		}
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("0") && attribute[i][v].contentEquals("1"))
			{
				pos1++;
				tot1++;
			}
			else
				tot1++;
		}
		
		if(tot==pos)
		{
			tree[tree_count]=sort_v+" 1 : 1";
			tree_count++;
			return 1;
		}
		else if(tot1==pos1)
		{
			tree[tree_count]=sort_v+" 1 : 0";
			tree_count++;
			return 1;
		}
		else 
		{
			//call id3 for attribute1
			
			for(i=0;i<attribute[0].length;i++)
			{
				if(attribute[0][i].contentEquals(sort_v))
				{
					
				}
				else
				{
					attribute1[0][m]=attribute[0][i];
					m++;
				}
			}
			
			
			m=1;
			for(i=1;i<attribute.length;i++)
			{
				n=0;flag=0;
				for(j=0;j<attribute[0].length;j++)
				{
					if(j!=v)
					{
						if(attribute[i][v].contentEquals("1"))
						{
							flag=1;
							attribute1[m][n]=attribute[i][j];
							n++;
							
						}
				}
			}
				if(flag==1)
				{
					m++;
				}
			}
			
			tree[tree_count]="(";
			tree_count++;
			tree[tree_count]=sort_v+" 1 : ";
			tree_count++;
			i=id3_calc(attribute1);
			tree[tree_count]=")";
			tree_count++;
			
			
		}
		tot=0;
		tot1=0;
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("1") && attribute[i][v].contentEquals("0"))
			{
				neg++;
				tot++;
			}
			else
				tot++;
		}
		for(i=1;i<attribute.length;i++)
		{
			if(attribute[i][len].contentEquals("0") && attribute[i][v].contentEquals("0"))
			{
				neg1++;
				tot1++;
			}
			else
				tot1++;
		}
		
		if(tot==neg)
		{
			tree[tree_count]=sort_v+" 0 : 1";
			tree_count++;
			return 1;
		}
		else if(tot1==neg1)
		{
			tree[tree_count]=sort_v+" 0 : 0";
			tree_count++;
			return 1;
		}
		else 
		{
			//call id3 for attribute2
			m=0;
			for(i=0;i<attribute[0].length;i++)
			{
				if(attribute[0][i].contentEquals(sort_v))
				{
					
				}
				else
				{
					attribute2[0][m]=attribute[0][i];
					m++;
				}
			}
			m=1;
			for(i=1;i<attribute.length;i++)
			{
				n=0;flag=0;
				for(j=0;j<attribute[0].length;j++)
				{
					if(j!=v)
					{
					if(attribute[i][v].contentEquals("0"))
					{
						flag=1;
						attribute2[m][n]=attribute[i][j];
						n++;
						
					}
					}
				}
				if(flag==1)
				{
					m++;
				}
			}
			
			tree[tree_count]="(";
			tree_count++;
			tree[tree_count]=sort_v+" 0 : ";
			tree_count++;
			i=id3_calc(attribute2);
			tree[tree_count]=")";
			tree_count++;
		}
		

		}
		return 1;
	}
	
	private static double log_base2(double d) {
		//calculating log base 2 of any value
		return Math.log(d) / Math.log(2);
		
	}
	private static double entropy(String[][] attribute,int size1,int size2) 
	{
		
		double entropy,log_pos,log_neg,neg,total,pos=0.0;
		int len;

		total=size1-1;
		int i=1;
		len=attribute[0].length-1;
		
		for(i=1;i<attribute.length;i++)
		{
			if(Integer.parseInt(attribute[i][len])==1)
			pos=pos+1;
		}
		neg=total-pos;
		log_pos=pos/total;
		log_neg=neg/total;
		
		if(pos==neg)
		{
			return 1.0;
		}
		else if(pos==total || neg==total)
		{
			return 0.0;
		}
		else 
		{
			entropy=-log_pos*log_base2(log_pos)-log_neg*log_base2(log_neg);
			return(entropy);
		}
	}
	
	private static String sort(String[] gain,double[] gain_v)
	{
    int n = gain_v.length;
    double temp = 0.0;
    String temp_s;
    if(gain_v.length==gain.length)
    {
        for(int i=0; i < n; i++)
        {
            for(int j=1; j < (n-i); j++)
            {
                    if(gain_v[j-1] < gain_v[j]){
                            //swap the elements!
                            temp = gain_v[j-1];
                            temp_s=gain[j-1];
                            gain_v[j-1] = gain_v[j];
                            gain[j-1]=gain[j];
                            gain_v[j] = temp;
                            gain[j]=temp_s;
                    }   
            }
        }
    }
    String ret;
    ret=gain_v[0]+"="+gain[0];
    
    return ret;
	}
	

}
