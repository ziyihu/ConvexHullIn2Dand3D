package convexhull2d;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class bruteforce extends JFrame implements ActionListener{
	static long b;
	JButton jButton2 = new JButton("Add 100 points");
	JButton jButton1 = new JButton("Redraw");
	JPanel p2 = new JPanel();
	JPanel contentPanel = (JPanel)getContentPane();
	static int i = 0;
	PointPanel1 pointPanel1 = new PointPanel1();
	static bruteforce convexHull;
	public static int k = 1;
	public static int time = 0;
	
	public bruteforce(){			
			ButtonGroup group = new ButtonGroup();	        
	        contentPanel.setLayout(new BorderLayout());
	        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	        p2.add(jButton1,BorderLayout.NORTH);	  
	        p2.add(jButton2,BorderLayout.NORTH);
	        contentPanel.add(pointPanel1,BorderLayout.CENTER);
	        jButton2.addActionListener(this); 
	        jButton1.addActionListener(this);
	        contentPanel.add(p2,BorderLayout.NORTH);	        
	        setSize(800,800);  
	        setLocation(100,100);  
	        setVisible(true);        
	}
	
	 public static void main(String[] args){  
		   if(i==0){			  
			   convexHull = new bruteforce();
	   	   }
	    } 
	
	public void actionPerformed(ActionEvent arg0){		
		 if(arg0.getSource().equals(jButton2)){
			time = 0;
			k++;
			contentPanel.repaint();
			}
		else if(arg0.getSource().equals(jButton1)){
			time = 0;
			k = 1;
			contentPanel.repaint();
		}		
	}
	   
	public static  List<MyVector> getTheConvexHull1(List pointList){
	   int i = pointList.size();	   
	   if(i<3) return null ;
	   long a = System.currentTimeMillis();
	   List<MyVector>myVectors = new ArrayList<MyVector>();
	   for(int j=0;j<i;j++){		   
		   for(int k = 0;k<i;k++){
			   MyVector myvector = null;
			   boolean valid = true;
			   for(int m = 0;m<i;m++){
				   Ellipse2D A =(Ellipse2D) pointList.get(j);
				   Ellipse2D B =(Ellipse2D) pointList.get(k);	
				   Ellipse2D C =(Ellipse2D) pointList.get(m);
				   Point startPoint = new Point((int)A.getX(),(int)A.getY());
				   Point endPoint = new Point((int)B.getX(),(int)B.getY());
				   Point cPoint = new Point((int)C.getX(),(int)C.getY());
				   myvector = new MyVector(startPoint, endPoint);			   
				   if(myvector.lieOnMyLeft(cPoint)){
					   valid = false;  
				   }
			   }
			   if(valid) myVectors.add(myvector);
		   }
	   }
	   b = System.currentTimeMillis()-a;
	   return myVectors;		
	}
	
	
	class PointPanel1 extends JPanel{
		List pointList;
		Color selectedColor;
		Ellipse2D selectedPoint;
		public PointPanel1(){
			pointList = new ArrayList();
			selectedColor = Color.LIGHT_GRAY;
			setBackground(null);		
		}
		
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			pointPanel1.removeAll();
			Font f = new Font("TimesRoman",Font.BOLD,18);
			g2.setFont(f);
			g2.drawString("Brute Force Convex hull", 20, 30);
			Font f1 = new Font("TimesRoman",Font.BOLD,15);
			g2.setFont(f1);
			int m = 100*k;
			g2.drawString("number of points:"+m, 370, 30);
			g2.drawString("total running time:"+b,370,50);
			Ellipse2D e;
			Color color;
			pointList.removeAll(pointList);
			for(int i = 0; i< 100*k;i++){
				  int xPoints[];
				  int yPoints[];
				  xPoints = new int[1000];
				  yPoints = new int[1000];
				  Random ran = new Random();
				  xPoints[i] = ran.nextInt(500)+100;
				  yPoints[i] = ran.nextInt(500)+100;
				  Point p1 = new Point();
				  Ellipse2D p2 = new Ellipse2D.Double(xPoints[i]-2,yPoints[i]-2,2,2);
				  g2.fill(p2);
				  pointList.add(p2);
			  }	  
			  for(int j = 0;j < pointList.size();j++){
				  e = (Ellipse2D)pointList.get(j);
				  if (e == selectedPoint)
					  color = selectedColor;
				  else color = Color.BLACK;
				  g2.setPaint(color);
				  g2.fill(e);		  
			  }			
			  List<MyVector> myVectors = convexHull.getTheConvexHull1(pointList);
				 for (int i = 0; i < myVectors.size(); i++){
					 g2.setColor(Color.black);
					 g2.drawLine(myVectors.get(i).startPoint.x, myVectors.get(i).startPoint.y, myVectors.get(i).endPoint.x, myVectors.get(i).endPoint.y);
							Font f3 = new Font("TimesRoman",Font.BOLD,10);
							g2.setFont(f3);
					if(myVectors.get(i).startPoint.x!=myVectors.get(i).endPoint.x){
						g2.setColor(Color.red);
						g2.drawString("("+myVectors.get(i).startPoint.x+","+myVectors.get(i).startPoint.y+")", myVectors.get(i).startPoint.x, myVectors.get(i).startPoint.y);
					}
				 }
			}
		
		public List getPointList(){
			return pointList;
		}
		
	}
}	
	
class MyVector{
	Point startPoint;
	Point endPoint;
	public MyVector(Point a ,Point b){
		super();
		startPoint = a;
		endPoint = b;
		}
	public boolean lieOnMyLeft(Point point){		
		int i = (endPoint.x-startPoint.x)*(point.y-startPoint.y)-(endPoint.y-startPoint.y)*(point.x-startPoint.x);		
		return i>0?true:false;			
		}
	}

