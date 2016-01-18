package convexhull2d;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.Button;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

public class quickhull extends Applet implements ActionListener{
	Random rnd;
    public static long time = 0;
    public static int k = 1;
    int pNum = 100;
    int xPoints[];
    int yPoints[];
    int xPoints2[];
    int yPoints2[];
    int num;
    int w, h;
    Button bt,bt1;
    
    public void init(){
    	Dimension size = getSize();
    	w = 500;
    	h = 500;
    	rnd = new Random();
    	xPoints = new int[1000];
    	yPoints = new int[1000];
    	num = 0;
    	xPoints2 = new int[1000];
    	yPoints2 = new int[1000];
    	quickconvexhull(1);
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
    		quickconvexhull(1);
		}
    	else if(ev.getSource() == bt1){
    		num = 0;
    		k++;
    		quickconvexhull(k);	
		}
		repaint();
    }
    //p is right to line ab
    public int right(int a, int b, int p){
    	return (xPoints[a] - xPoints[b])*(yPoints[p] - yPoints[b]) - (xPoints[p] - xPoints[b])*(yPoints[a] - yPoints[b]);
    }
    //distance from point p to line ab
    public float distance(int a, int b, int p){
    	float x, y, u;
    	u = (((float)xPoints[p] - (float)xPoints[a])*((float)xPoints[b] - (float)xPoints[a]) + ((float)yPoints[p] - (float)yPoints[a])*((float)yPoints[b] - (float)yPoints[a])) 
	    / (((float)xPoints[b] - (float)xPoints[a])*((float)xPoints[b] - (float)xPoints[a]) + ((float)yPoints[b] - (float)yPoints[a])*((float)yPoints[b] - (float)yPoints[a]));
    	x = (float)xPoints[a] + u * ((float)xPoints[b] - (float)xPoints[a]);
    	y = (float)yPoints[a] + u * ((float)yPoints[b] - (float)yPoints[a]);
    	return ((x - (float)xPoints[p])*(x - (float)xPoints[p]) + (y - (float)yPoints[p])*(y - (float)yPoints[p]));
    }
   public int farthestpoint(int a, int b, ArrayList<Integer>al){
	   float maxD, dis;
	   int maxP, p;
	   maxD = -1;
	   maxP = -1;
	   for(int i = 0; i < al.size(); i++){
		   p = al.get(i);
		   if ((p == a) || (p == b)){
			   continue;
		   }
		   dis = distance(a, b, p);
		   if (dis > maxD){
			   maxD = dis;
			   maxP = p;
		   }
	   }
	   return maxP;
    }
   public void quickhull(int a, int b, ArrayList<Integer>al){
	   if(al.size() == 0){
		   return;
	   }
	   int c, p;
	   c = farthestpoint(a, b, al);
	   ArrayList<Integer> al1 = new ArrayList<Integer>();
	   ArrayList<Integer> al2 = new ArrayList<Integer>();
	   for(int i=0; i<al.size(); i++){
		   p = al.get(i);
	   if((p == a) || (p == b)){
		   continue;
	   }
	   if(right(a,c,p) > 0){
		   al1.add(p);
	   }
	   else if(right(c,b,p) > 0){
		   al2.add(p);
	   }
	}
	   quickhull(a, c, al1);
	   xPoints2[num] = xPoints[c];
	   yPoints2[num] = yPoints[c];
	   num++;
	   quickhull(c, b, al2);
    }

    public void quickconvexhull(int k){
    	int x, y;
    	for(int i = 0; i < pNum*k; i++){
    		xPoints[i] = rnd.nextInt(500)+100;
    		yPoints[i] = rnd.nextInt(500)+100;
    	}
    	long a = System.currentTimeMillis();
    	int r, l;
    	r = l = 0;
    	for(int i = 1; i < pNum*k; i++){
	    if((xPoints[r] > xPoints[i]) || (xPoints[r] == xPoints[i] && yPoints[r] > yPoints[i])){
	    	r = i;
	    }
	    if((xPoints[l] < xPoints[i]) || (xPoints[l] == xPoints[i] && yPoints[l] < yPoints[i])){
	    	l = i;
	    }
	}
	ArrayList<Integer> al1 = new ArrayList<Integer>();
	ArrayList<Integer> al2 = new ArrayList<Integer>();
	int upper;
	for(int i = 0; i < pNum*k; i++){
	    if((i == l) || (i == r)){
	    	continue;
	    }
	    upper = right(r,l,i);
	    if(upper > 0){
	    	al1.add(i);
	    }
	    else if(upper < 0){
	    	al2.add(i);
	    }
	}
		xPoints2[num] = xPoints[r];
		yPoints2[num] = yPoints[r];
		num++;
		quickhull(r, l, al1);
		xPoints2[num] = xPoints[l];
		yPoints2[num] = yPoints[l];
		num++;
		quickhull(l, r, al2);
		long b = System.currentTimeMillis();
		time = b - a;
    }
    public void paint(Graphics g){
    	setSize(800, 800);
    	Font f = new Font("TimesRoman",Font.BOLD,20);	
    	g.setFont(f);
    	g.drawString("Quick hull",30,60);
    	Font f1 = new Font("TimesRoman",Font.BOLD,15);
    	int num1 = 100 * k;
		g.setFont(f1);
		g.drawString("number of points:"+num1,370,60);	
		Font f2 = new Font("TimesRoman",Font.BOLD,15);
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