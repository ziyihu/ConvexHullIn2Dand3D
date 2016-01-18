package convexhull2d;
import java.applet.Applet;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class gramham extends Applet implements ActionListener{
	public static long time = 0;
	Random rnd;
	public static int k = 1;
	int pNum = 100;
	int xPoints[];
	int yPoints[];
	int num;
	int xPoints2[];
	int yPoints2[];
	int w, h;
	Button bt,bt1;

	class pData implements Comparable<pData>{
		int index;
		double angle;
		long distance;
		pData(int i, double a, long d){
			index = i;
			angle = a;
			distance = d;
		}
		// for sorting
		public int compareTo(pData p){
			if(this.angle < p.angle){
				return -1;
			}
			else if(this.angle > p.angle){
				return 1;
			}
			else{
				if(this.distance < p.distance){
					return -1;
				}
				else if(this.distance > p.distance){
					return 1;
				}
			}
			return 0;
		}
	}

	public void init(){
		Dimension size = getSize();
		w = size.width;
		h = size.height;
		rnd = new Random();
		xPoints = new int[1000];
		yPoints = new int[1000];
		grahamsScan(1);
		bt = new Button("redraw");
		bt1 = new Button("add 100 points");
		bt.addActionListener(this);
		bt1.addActionListener(this);
		add(bt,BorderLayout.SOUTH);
		add(bt1,BorderLayout.SOUTH);
	}

	  public void actionPerformed(ActionEvent ev){
			if(ev.getSource() == bt){
			    num = 0;
			    k = 1;		    
			    grahamsScan(1);
			}
			else if(ev.getSource() == bt1){
				num = 0;
				k++;
				grahamsScan(k);
			}
			repaint();
	  }

	  public double angle(int o, int a){
		  return Math.atan((double)(yPoints[a] - yPoints[o])/(double)(xPoints[a] - xPoints[o]));
	  }
	  
	  public long distance(int a, int b){
		  return ((xPoints[b] - xPoints[a])*(xPoints[b] - xPoints[a]) + (yPoints[b] - yPoints[a])*(yPoints[b] - yPoints[a]));
	  }

	  public int ccw(int p1, int p2, int p3){
		  return (xPoints[p2] - xPoints[p1])*(yPoints[p3] - yPoints[p1]) - (yPoints[p2] - yPoints[p1])*(xPoints[p3] - xPoints[p1]);
	  }

	  public void swap(int[] stack, int a, int b){
		  int tmp = stack[a];
		  stack[a] = stack[b];
		  stack[b] = tmp;
	  }

	  public void grahamsScan(int k)
	  {
		  int x, y;
		  for(int i = 0; i < pNum*k; i++){
			  xPoints[i] = rnd.nextInt(500)+100;
			  yPoints[i] = rnd.nextInt(500)+100;
		  }
		  long a = System.currentTimeMillis();
		  int min = 0;
		  for(int i = 1; i < pNum*k; i++){
			  if(yPoints[i] == yPoints[min]){
				  if(xPoints[i] < xPoints[min]){
					  min = i;
				  }
			  }
			  else if(yPoints[i] < yPoints[min]){
				  min = i;
			  }
		  }
		  ArrayList<pData> al = new ArrayList<pData>();
		  double ang;
		  long dist;
		  for(int i = 0; i < pNum*k; i++){
			  if(i ==  min){
				  continue;
			  }
			  ang = angle(min, i);
			  if (ang < 0){
				  ang += Math.PI;
			  }
			  dist = distance(min, i);
			  al.add(new pData(i, ang, dist));
		  }
		  Collections.sort(al);	
		  int stack[] = new int[pNum*k+1];
		  int j = 2;
		  for(int i = 0; i < pNum*k; i++){
			  if(i == min){
				  continue;
			  }
			  pData data = al.get(j-2);
			  stack[j] = data.index;
			  j++;
		  }
		  stack[0] = stack[pNum*k];
		  stack[1] = min;
		  int tmp;
		  int M = 2;
		  for(int i = 3; i <= pNum*k; i++){
			  while(ccw(stack[M-1], stack[M], stack[i]) <= 0){
			  M--;
			  }
			  M++;
			  swap(stack, i, M);
		  }
		  num = M;
		  xPoints2 = new int[1000];
		  yPoints2 = new int[1000];
		  for(int i = 0; i < num*k; i++){
			  xPoints2[i] = xPoints[stack[i+1]];
			  yPoints2[i] = yPoints[stack[i+1]];
		  }
		  long b = System.currentTimeMillis();
		  time = b - a;
	  }

	  public void paint(Graphics g){
	 	setSize(800, 800); 
		Font f = new Font("TimesRoman",Font.BOLD,20);	
		g.setFont(f);
		g.drawString("Gramham convex hull",30,60);
		Font f1 = new Font("TimesRoman",Font.BOLD,15);
		int num1 = 100 * k;
		g.setFont(f1);
		g.drawString("number of points:"+num1,370,60);
		Font f2 = new Font("TimesRoman",Font.BOLD,16);
		g.setFont(f1);
		g.drawString("total running time:"+time,370,80);
		g.setColor(Color.BLACK);
		for(int i = 0; i < pNum*k; i++){
		    g.fillOval(xPoints[i]-2,yPoints[i]-2, 3,3);	   
		}
		g.setColor(Color.BLACK);
		g.drawPolygon(xPoints2, yPoints2, num);
		g.setColor(Color.red);
		for(int i = 0; i < num; i++){
			Font f3 = new Font("TimesRoman",Font.BOLD,10);
			g.setFont(f3);
			g.drawString("("+xPoints2[i]+","+yPoints[i]+")", xPoints2[i], yPoints2[i]);
		    g.drawOval(xPoints2[i]-5,yPoints2[i]-5, 10,10);
		}
	    }
}