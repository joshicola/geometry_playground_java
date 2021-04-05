/* *********************************************************************************  
 *   This file is part of GeometryPlayground.
 *
 *   GeometryPlayground is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   GeometryPlayground is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with GeometryPlayground.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
 
import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

abstract class ToroidalConstruct extends GeoConstruct {
	protected double[] v1={0,0,0},v2={0,0,0};
  protected static double scale=0;//Powers of two
  public static final int scaleLimit=5;
  public ToroidalConstruct(int t, LinkedList<GeoConstruct> clickedList){
	  type=t;
	  for(int i=0;i<clickedList.size();i++)
		  constList.addLast(clickedList.get(i));
	  if (constList.size()==2) {
	    double[] u={0,0,0}, v={0,0,0};
	    constList.get(0).getXYZ(u);
        constList.get(1).getXYZ(v);
        this.setXYZ(u, v);
	  }
  }
  public ToroidalConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v){
	  type=t;
	  for(int i=0;i<clickedList.size();i++)
		  constList.addLast(clickedList.get(i));
	  if (t==ANGLE) {
		double[] u={0,0,0};
		constList.get(0).getXYZ(v);
		constList.get(2).getXYZ(u);
		resetSecondVector(v,u);
		v[0]=(v[0]+u[0])/2;
		v[1]=(v[1]+u[1])/2;
		fundamentalDomain(v);
	  }
	  if (t==DISTANCE || t==RATIO || t==SUM || t==DIFF || t==PROD) {
		double[] u={0,0,0};
		constList.get(0).getXYZ(v);
		constList.get(1).getXYZ(u);
		resetSecondVector(v,u);
		v[0]=(v[0]+u[0])/2;
		v[1]=(v[1]+u[1])/2;
		fundamentalDomain(v);
	  }
	  if (t==TRIANGLE) {
		double[] u={0,0,0},w={0,0,0};;
		constList.get(0).getXYZ(v);
		constList.get(1).getXYZ(u);
		constList.get(2).getXYZ(w);
		resetSecondVector(v,u);
		resetSecondVector(v,w);
		v[0]=(u[0]+w[0]+v[0])/3;
		v[1]=(u[1]+w[1]+v[1])/3;
		fundamentalDomain(v);
	  }
	  if (t==CONSTANT) {
		measureValue=v[2];
		measureValueNew=v[2];
		v[2]=0;
		fundamentalDomain(v);
	  }
	  setXYZ(v);
	  update();
  }
  public abstract ToroidalPoint intersect(int m, ToroidalConstruct a);

  public void setXYZ(double[] v) {
    x=v[0];		y=v[1];		z=0;
	newX=x;		newY=y;		newZ=z;
  }

  public void setXYZ(double[] u, double[] v) {
    resetSecondVector(u,v);
    if (type==LINE || type==SEGMENT) {x=v[0]-u[0];y=v[1]-u[1];z=0;}
    else if (type==PERP) {x=-u[1];y=u[0];z=0;}
    else if (type==PARLL0){x=u[0];y=u[1];z=0;}
    setValid(true);
  }

  public void setNewXYZ(double[] v) {
    newX=v[0];	newY=v[1];	newZ=v[2];
  }

  public void setNewXYZ(double[] u, double[] v) {
	resetSecondVector(u,v);
	if (type==LINE || type==SEGMENT) {newX=v[0]-u[0];newY=v[1]-u[1];newZ=0;}
    else if (type==PERP) {newX=-u[1];newY=u[0];newZ=0;}
    else if (type==PARLL0){newX=u[0];newY=u[1];newZ=0;}
  }

  public void translate(double[] dragStart, double[] dragNow){
    double[] v={0,0,0};
    getXYZ(v);
	for(int i=0;i<3;i++) {
	  v[i]+=dragNow[i]-dragStart[i];
	  fundamentalDomain(v);
	}
    this.setNewXYZ(v);
  }
  public static double getScale() {return 1;}
  public static void setScale(double s){resetScale();}
  public static void resetScale(){scale=0;}
  public static void rescale(double[] v){}
  public static void unscale(double[] v){}
  public void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow) {
    MathEqns.transform(fixedObject,this,dragStart,dragNow);
  }
  public void fundamentalDomain(double[] v) {
	  while (v[0]<0) v[0]+=1;	while (v[0]>=1) v[0]-=1;
	  while (v[1]<0) v[1]+=1;	while (v[1]>=1) v[1]-=1;
  }
  public double resetSecondVector(double[] u, double[] v) {
	  double dist=2;
	  int horiz=0,vert=0;
	  for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
		  if (Math.sqrt((v[0]-u[0]+i)*(v[0]-u[0]+i)+(v[1]-u[1]+j)*(v[1]-u[1]+j))<dist) {
			dist=Math.sqrt((v[0]-u[0]+i)*(v[0]-u[0]+i)+(v[1]-u[1]+j)*(v[1]-u[1]+j));
			horiz=i; vert=j;
		  }
	  v[0]+=horiz;
	  v[1]+=vert;
	  return dist;
  }
} // end class 


class ToroidalPoint extends ToroidalConstruct{
	protected int sz,cs;
  public ToroidalPoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}


  public ToroidalPoint intersect(int m, ToroidalConstruct a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	ToroidalPoint newPoint=new ToroidalPoint(LINEintLINE0,tempList,v1);
    return newPoint;
  }

  public void draw(Graphics g, int SZ, boolean New) {
    sz=(int)MathEqns.max(4,SZ/40);
    cs=GeoPlayground.getHght();
    if ((New && isRealNew) || (!New && isReal)) {
      int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
  	  if(New){
        getNewXYZ(v1);
      }
      else{
        getXYZ(v1);
      }
	  fundamentalDomain(v1);
	  if (getType()<30)
	  switch(GeoPlayground.model) {
	    case 0:
		    g.fillOval(MathEqns.round(v1[0]*cs)-sz/2+fudge,MathEqns.round(v1[1]*cs)-sz/2,sz,sz);
		    if (getLabelShown())
		      g.drawString(displayText,
			               MathEqns.round(v1[0]*cs)-sz/2+fudge,MathEqns.round(v1[1]*cs)-sz/2);
		  break;
		case 1:
			sz=(int)MathEqns.max(sz-2,2);
			for (int i=0;i<3;i++) for (int j=0;j<3;j++) {
				g.fillOval(MathEqns.round(v1[0]*cs/3)-sz/2+fudge+2*SZ/3*i,MathEqns.round(v1[1]*cs/3)-sz/2+2*SZ/3*j,sz,sz);
			    if (getLabelShown()&& i==1 && j==1)
			      g.drawString(displayText,
				               MathEqns.round(v1[0]*cs/3)-sz/2+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)-sz/2+2*SZ/3);
			}
			break;
		case 2:
		case 3:
			v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
			  v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
			  if (v1[1]<=.5) {
				  g.fillOval(MathEqns.round(v2[0])-sz/2+fudge,MathEqns.round(v2[1])-sz/2,sz,sz);
			      if (getLabelShown())
			        g.drawString(displayText,
					             MathEqns.round(v2[0])-sz/2+fudge,MathEqns.round(v2[1])-sz/2);
			}
			  break;
	  }
    }
  }

  public boolean mouseIsOver(double[] v, int SZ){
    double sz=MathEqns.max(6,SZ/20);
	int cs=GeoPlayground.getHght();
	return ((x-v[0])*(x-v[0])+(y-v[1])*(y-v[1])<(sz*sz/cs/cs));
  }

  public void updatePTonLINE(){
	double[] nm={0,0,0},bn={0,0,0};
	double dist=2;
	int min=0;
	setValidNew(get(0).getValidNew());
	if (getValidNew()) {
		constList.get(0).get(0).getNewXYZ(v2);
		constList.get(0).get(1).getNewXYZ(bn);
		if (constList.get(0).getType()==PERP ||
			constList.get(0).getType()==BISECTOR ||
			constList.get(0).getType()==PARLL0) {
			constList.get(0).getNewXYZ(nm);
			dist=MathEqns.norm(nm);
			v2[0]=bn[0]+constList.get(0).getNewX()/dist*.49;
			v2[1]=bn[1]+constList.get(0).getNewY()/dist*.49;
			v2[2]=0;
		}
		resetSecondVector(v2,bn);
		dist=2;
		int j=-400, k=500; if (constList.get(0).getType()==SEGMENT) {j=2;k=98;}
		for (int i=j;i<=k;i++) {
			nm[0]=v2[0]*i/100.+bn[0]*(100.-i)/100.;
			nm[1]=v2[1]*i/100.+bn[1]*(100.-i)/100.;
			fundamentalDomain(nm);
			if (MathEqns.norm(v1,nm)<dist) {
				dist=MathEqns.norm(v1,nm);
				min=i;
			}
		}
		nm[0]=v2[0]*min/100.+bn[0]*(100.-min)/100.;
		nm[1]=v2[1]*min/100.+bn[1]*(100.-min)/100.;
		fundamentalDomain(nm);
		nm[2]=0;
		setNewXYZ(nm);
    }
  }
  public void updatePTonCIRC() {
	double dist=2;
	setValidNew(get(0).getValidNew());
	if (getValidNew()) {
		double[] center={0,0,0},point={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0};
	    constList.get(0).get(0).getNewXYZ(center);
	    constList.get(0).get(1).getNewXYZ(point);
	    w[0]=center[0]+.5;
	    w[1]=center[1]+.5;
	    double r=2;
	    dist=r;
        for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
        	v[0]=center[0]+i;
        	v[1]=center[1]+j;
        	if (MathEqns.norm(point,v)<r) r=MathEqns.norm(point,v);
	    }
	    if (r<.5) {
	    	for (int k=0;k<710;k++) {
	    		u[0]=center[0]+r*Math.cos(k/113.);
	    		u[1]=center[1]+r*Math.sin(k/113.);
	    		for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
	    			v[0]=v1[0]+i;
	    			v[1]=v1[1]+j;
	    			if (MathEqns.norm(v,u)<dist) {
	    				dist=MathEqns.norm(u,v);
	    				w[0]=u[0];	w[1]=u[1];
	    			}
	    		}
	    	}
	    }
	    else {
	    	double theta=Math.acos(.5/r);
	    	for (double k=theta;k<Math.PI/2-theta;k+=(Math.PI/2-2*theta)/10.) 
	    		for (int l=0;l<4;l++) {
	    			u[0]=center[0]+r*Math.cos(k+l*Math.PI/2);
	    			u[1]=center[1]+r*Math.sin(k+l*Math.PI/2);
	    			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
	    				v[0]=v1[0]+i;
	    				v[1]=v1[1]+j;
	    				if (MathEqns.norm(v,u)<dist) {
	    					dist=MathEqns.norm(u,v);
	    					w[0]=u[0];	w[1]=u[1];
	    				}
	    			}
	    		}
	    }
	    fundamentalDomain(w);
	    setNewXYZ(w);
	}
		
  }
  public void update() {
	boolean nowValid=true;
	for (int i=0;i<constList.size();i++) nowValid = (nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);	
	getNewXYZ(v1);
	for (int i=0;i<2;i++) v1[i]=(double)MathEqns.round(GeoPlayground.getHght()*v1[i])/GeoPlayground.getHght();
	fundamentalDomain(v1);
	setNewXYZ(v1);
  }
}

class ToroidalLine extends ToroidalConstruct{
  double[] vec1={0,0,0}, vec2={0,0,0};
  int cs;
  public ToroidalLine(int t, LinkedList<GeoConstruct> clickedList){
    super(t,clickedList);
  }
  public ToroidalPoint intersect(int m, ToroidalConstruct a){
    ToroidalPoint newPoint;
    if(a.getType()==0)
      newPoint=intersect(m,(ToroidalCircle)a);
    else
      newPoint=intersect(m,(ToroidalLine)a);
    return newPoint;
  }
  public ToroidalPoint intersect(int m, ToroidalLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	ToroidalPoint newPoint=new ToroidalPoint(LINEintLINE0,tempList,v1);
    return newPoint;	// intersections not implemented in Toroidal
  }
  public ToroidalPoint intersect(int m, ToroidalCircle a){
    ToroidalPoint newPoint=a.intersect(m,this);
    return newPoint;	// intersections not implemented in Toroidal
  }
  
  public void draw(Graphics g, int SZ, boolean New) {
    if (isReal) {
      double[] a={0,0,0};
      if(New){
        constList.get(0).getNewXYZ(vec1);
        constList.get(1).getNewXYZ(vec2);
		getNewXYZ(a);
      }
      else{
        constList.get(0).getXYZ(vec1);
        constList.get(1).getXYZ(vec2);
		getXYZ(a);
      }
	  cs=GeoPlayground.getHght();
	  if (type==PERP || type==BISECTOR || type==PARLL0) {
	    vec1[0]=vec2[0]+a[0];
		vec1[1]=vec2[1]+a[1];
	  }
	  resetSecondVector(vec1,vec2);
	  if (type!=SEGMENT) {
	    double a0=vec2[0]-vec1[0],
		       a1=vec2[1]-vec1[1],
			   dd=Math.sqrt(a0*a0+a1*a1);
		int b0=MathEqns.round(Math.abs(a0/dd*24)),
		    b1=MathEqns.round(Math.abs(a1/dd*24)), gcd=1;
		for (int i=2;i<=MathEqns.max(b0,b1);i++) if (b0%i==0 && b1%i==0) gcd=i;
		b0/=gcd; b1/=gcd;
		int rd=g.getColor().getRed(),
		    gr=g.getColor().getGreen(),
			bl=g.getColor().getBlue();
		int upTo=0;
		if (b0!=0 && b1!=0) upTo=MathEqns.round(MathEqns.min(Math.abs(48*dd/a0*b0),Math.abs(48*dd/a1*b1)));
		else if (b0!=0) upTo=MathEqns.round(Math.abs(64*dd/a0));
		else if (b1!=0) upTo=MathEqns.round(Math.abs(64*dd/a1));
		for (int j=-(upTo/2);j<=0;j++) for (int jj=0;jj<2;jj++) {
		int i=j; if (jj==1) i=-j;
		  double k=(double)Math.abs(i)*2/upTo;
		  g.setColor(new Color((int)(rd*(1-k)+255*k),
		                       (int)(gr*(1-k)+255*k),
							   (int)(bl*(1-k)+255*k)));	//*/ 
		  v1[0]=vec1[0]*(i)/32/dd+(vec2[0])*(32*dd-i)/32/dd;
		  v1[1]=vec1[1]*(i)/32/dd+(vec2[1])*(32*dd-i)/32/dd;
		  v2[0]=vec1[0]*(1+i)/32/dd+(vec2[0])*(32*dd-1-i)/32/dd;
		  v2[1]=vec1[1]*(1+i)/32/dd+(vec2[1])*(32*dd-1-i)/32/dd;
		  fundamentalDomain(v1);
	      fundamentalDomain(v2);
		  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		  switch(GeoPlayground.model) {
		    case 0:
			  if (MathEqns.norm(v1,v2)<.5)
			    g.drawLine(MathEqns.round(cs*v1[0])+fudge,MathEqns.round(cs*v1[1]),
				           MathEqns.round(cs*v2[0])+fudge,MathEqns.round(cs*v2[1]));
			  if (i==3 && getLabelShown())
			    g.drawString(displayText,MathEqns.round(cs*v1[0])+fudge,MathEqns.round(cs*v1[1]));
		      break;
		    case 1:
				for (int m=0;m<3;m++) for (int n=0;n<3;n++) {
				if (MathEqns.norm(v1,v2)<.5)
				  g.drawLine(MathEqns.round(cs*v1[0]/3)+fudge+2*SZ/3*m,MathEqns.round(cs*v1[1]/3)+2*SZ/3*n,
					         MathEqns.round(cs*v2[0]/3)+fudge+2*SZ/3*m,MathEqns.round(cs*v2[1]/3)+2*SZ/3*n);
				if (i==3 && getLabelShown() && m==1&&n==1)
				  g.drawString(displayText,
						  MathEqns.round(cs*v1[0]/3)+fudge+2*SZ/3,MathEqns.round(cs*v1[1]/3)+2*SZ/3);
				}
				break;
		    case 2:
		    case 3:
		    	int[] w1={0,0,0};
				w1[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3);
				w1[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3);
				if (v2[1]<=.5 && v1[1]<=.5) {
				    int[] w2={0,0,0};
				    w2[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v2[1]))*Math.cos(2*Math.PI*v2[0])/3);
				    w2[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v2[1]))*Math.sin(2*Math.PI*v2[0])/3);
				    g.drawLine(w1[0]+fudge,w1[1],w2[0]+fudge,w2[1]);
				    if (i==7 && getLabelShown())
					  g.drawString(displayText,w1[0]+fudge,w1[1]);
				}
				else g.drawOval(w1[0]+fudge,w1[1],1,1);
			    break;
		  }
		}
	  }
    }
  }  

  public boolean mouseIsOver(double[] v0, int SZ){
    double[] v2={0,0,0},v1={0,0,0},r={0,0,0};
    constList.get(1).getXYZ(v2);
	double dist=2;
	if (type==PERP || type==BISECTOR || type==PARLL0) {
	  v1[0]=v2[0]+(x/2);
	  v1[1]=v2[1]+(y/2);
	  v1[2]=0;
	  v2[0]-=(x/2);
	  v2[1]-=(y/2);
	}
	else {//(type==LINE || type==SEGMENT)
	  constList.get(0).getXYZ(v1);
	}
	resetSecondVector(v1,v2);
	dist=2;
	int j=-400,k=500; if (constList.get(0).getType()==SEGMENT) {j=0;k=100;}
	for (int i=j;i<=k;i++) {
	  r[0]=v1[0]*i/100+v2[0]*(100-i)/100;
	  r[1]=v1[1]*i/100+v2[1]*(100-i)/100;
	  fundamentalDomain(r);
	  if (MathEqns.norm(r,v0)<dist) dist=MathEqns.norm(r,v0);
	}
    return (dist<.0125);
  }
  public void update() {
    boolean nowValid=true;
	for (int i=0;i<getSize();i++) nowValid=(nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (nowValid) {
        constList.get(0).getNewXYZ(v1);
        constList.get(1).getNewXYZ(v2);
		resetSecondVector(v1,v2);
		if (type==LINE || type==SEGMENT) {
        	double[] norm={0,0,0};
		    norm[0]=v2[0]-v1[0];
		    norm[1]=v2[1]-v1[1];
		    norm[2]=0;
		    if (MathEqns.norm(norm)<.00001) setValidNew(false);
            else setNewXYZ(norm);
        }
      }
  }
}

class ToroidalCircle extends ToroidalConstruct{

  public ToroidalCircle(int t, LinkedList<GeoConstruct> clickedList) {
	super(t,clickedList);
	if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
  }
  public ToroidalPoint intersect(int m, ToroidalConstruct a){
    ToroidalPoint newPoint;
    if(a.getType()==0)
      newPoint=intersect(m,(ToroidalCircle)a);
    else
      newPoint=intersect(m,(ToroidalLine)a);
    return newPoint;
  }
  public ToroidalPoint intersect(int m, ToroidalLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	ToroidalPoint newPoint=new ToroidalPoint(CIRCintLINE0+m,tempList,v1);
    return newPoint;	// intersections not implemented in Toroidal
  }

  public ToroidalPoint intersect(int m, ToroidalCircle a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	ToroidalPoint newPoint=new ToroidalPoint(CIRCintCIRC00+m,tempList,v1);
    return newPoint;	// intersections not implemented in Toroidal
  }

  public void draw(Graphics g, int SZ, boolean New) {
    if ((New && isRealNew) || (!New && isReal)) {
      double[] center={0,0,0},radial={0,0,0},u={0,0,0},v={0,0,0};
      int cs=GeoPlayground.getHght();
	  if(New){
        constList.get(0).getNewXYZ(center);
        constList.get(1).getNewXYZ(radial);
      }
      else{
        constList.get(0).getXYZ(center);
        constList.get(1).getXYZ(radial);
      }
      double r=2;
	  for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
	    u[0]=radial[0];
		u[1]=radial[1];
		v[0]=center[0]+i;
		v[1]=center[1]+j;
	    if (MathEqns.norm(u,v)<r) {
		  r=MathEqns.norm(u,v);
		}
	  }
	  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	  if (getShown()) switch(GeoPlayground.model) {
	    case 0:
		  if (r<=.5) {
			for (int i=0;i<176;i++) {
			  int x1=MathEqns.round(cs*(center[0]+r*Math.cos(i/28.))),
			      y1=MathEqns.round(cs*(center[1]+r*Math.sin(i/28.))),
				  x2=MathEqns.round(cs*(center[0]+r*Math.cos((i+1)/28.))),
				  y2=MathEqns.round(cs*(center[1]+r*Math.sin((i+1)/28.)));
			  while (x1<0) x1+=cs; while (x1>=cs) x1-=cs;
			  while (x2<0) x2+=cs; while (x2>=cs) x2-=cs;
			  while (y1<0) y1+=cs; while (y1>=cs) y1-=cs;
			  while (y2<0) y2+=cs; while (y2>=cs) y2-=cs;
			  if (Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))<SZ)
			    g.drawLine(x1+fudge,y1,x2+fudge,y2);
			}
			if (getLabelShown())
			  g.drawString(displayText,
			               MathEqns.round(cs*(center[0]+r*.707))%cs+fudge,
			               MathEqns.round(cs*(center[1]+r*.707))%cs);
			}
		  else {
			int theta=(int)(180*Math.acos(.5/r)/Math.PI);
			for (int i=theta;i<90-theta;i++) for (int j=0;j<4;j++) {
			  int x1=(int)(cs*(center[0]+r*Math.cos(i*Math.PI/180+j*Math.PI/2))),
			      y1=(int)(cs*(center[1]+r*Math.sin(i*Math.PI/180+j*Math.PI/2))),
				  x2=(int)(cs*(center[0]+r*Math.cos((i+1)*Math.PI/180+j*Math.PI/2))),
			      y2=(int)(cs*(center[1]+r*Math.sin((i+1)*Math.PI/180+j*Math.PI/2)));
			  while (x1<0) x1+=cs; while (x1>cs) x1-=cs;
			  while (x2<0) x2+=cs; while (x2>cs) x2-=cs;
			  while (y1<0) y1+=cs; while (y1>cs) y1-=cs;
			  while (y2<0) y2+=cs; while (y2>cs) y2-=cs;
			  if (Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))<SZ) g.drawLine(x1+fudge,y1,x2+fudge,y2);
			}
			if (getLabelShown()) {
			  g.drawString(displayText,
			               MathEqns.round(cs*(center[0]+r*.707))%cs+fudge,
			               MathEqns.round(cs*(center[1]+r*.707))%cs);
			}
		  }
		  break;
		case 1:
			if (r<=.5) {
				for (int i=0;i<176;i++) {
				  int x1=MathEqns.round(cs*(center[0]+r*Math.cos(i/28.))),
				      y1=MathEqns.round(cs*(center[1]+r*Math.sin(i/28.))),
					  x2=MathEqns.round(cs*(center[0]+r*Math.cos((i+1)/28.))),
					  y2=MathEqns.round(cs*(center[1]+r*Math.sin((i+1)/28.)));
				  while (x1<0) x1+=cs; while (x1>=cs) x1-=cs;
				  while (x2<0) x2+=cs; while (x2>=cs) x2-=cs;
				  while (y1<0) y1+=cs; while (y1>=cs) y1-=cs;
				  while (y2<0) y2+=cs; while (y2>=cs) y2-=cs;
				  if (Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))<SZ)
				    for (int m=0;m<3;m++) for (int n=0;n<3;n++)
				    	g.drawLine(x1/3+fudge+2*SZ/3*m,y1/3+2*SZ/3*n,x2/3+fudge+2*SZ/3*m,y2/3+2*SZ/3*n);
				}
				if (getLabelShown()) {
					if ((center[0]+r*.707)>1) center[0]-=1;
					if ((center[1]+r*.707)>1) center[1]-=1;
				  g.drawString(displayText,
				               MathEqns.round(cs/3*(center[0]+r*.707))%cs+fudge+2*SZ/3,
				               MathEqns.round(cs/3*(center[1]+r*.707))%cs+2*SZ/3);
				}
				}
			  else {
				int theta=(int)(180*Math.acos(.5/r)/Math.PI);
				for (int i=theta;i<90-theta;i++) for (int j=0;j<4;j++) {
				  int x1=(int)(cs*(center[0]+r*Math.cos(i*Math.PI/180+j*Math.PI/2))),
				      y1=(int)(cs*(center[1]+r*Math.sin(i*Math.PI/180+j*Math.PI/2))),
					  x2=(int)(cs*(center[0]+r*Math.cos((i+1)*Math.PI/180+j*Math.PI/2))),
				      y2=(int)(cs*(center[1]+r*Math.sin((i+1)*Math.PI/180+j*Math.PI/2)));
				  while (x1<0) x1+=cs; while (x1>cs) x1-=cs;
				  while (x2<0) x2+=cs; while (x2>cs) x2-=cs;
				  while (y1<0) y1+=cs; while (y1>cs) y1-=cs;
				  while (y2<0) y2+=cs; while (y2>cs) y2-=cs;
				  if (Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))<SZ)
					  for (int m=0;m<3;m++) for (int n=0;n<3;n++)
					    	g.drawLine(x1/3+fudge+2*SZ/3*m,y1/3+2*SZ/3*n,x2/3+fudge+2*SZ/3*m,y2/3+2*SZ/3*n);
				}
				if (getLabelShown()) {
					if ((center[0]+r*.707)>1) center[0]-=1;
					if ((center[1]+r*.707)>1) center[1]-=1;
				  g.drawString(displayText,
				               MathEqns.round(cs/3*(center[0]+r*.707))%cs+fudge+2*SZ/3,
				               MathEqns.round(cs/3*(center[1]+r*.707))%cs+2*SZ/3);
				}
			  }
			  break;
		case 2:
		case 3:
			if (r<=.5)
				  for (int i=0;i<88;i++) {
				    u[0]=center[0]+r*Math.cos(i/14.);
					u[1]=center[1]+r*Math.sin(i/14.);
					fundamentalDomain(u);
					v[0]=center[0]+r*Math.cos((i+1)/14.);
					v[1]=center[1]+r*Math.sin((i+1)/14.);
					fundamentalDomain(v);
					int[] w1={0,0,0};
					w1[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*u[1]))*Math.cos(2*Math.PI*u[0])/3);
					w1[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*u[1]))*Math.sin(2*Math.PI*u[0])/3);
					if (v[1]<=.5 && u[1]<=.5) {
					  int[] w2={0,0,0};
					  w2[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v[1]))*Math.cos(2*Math.PI*v[0])/3);
					  w2[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v[1]))*Math.sin(2*Math.PI*v[0])/3);
					  g.drawLine(w1[0]+fudge,w1[1],w2[0]+fudge,w2[1]);
					  if (i==11 && getLabelShown())
						g.drawString(displayText,w1[0]+fudge,w1[1]);
					}
					else g.drawOval(w1[0]+fudge,w1[1],1,1);
				  }
				  else {
				    double theta=Math.acos(.5/r);
				    for (int l=0;l<4;l++) 
					for (int i=0;i<32;i++) {
					  u[0]=center[0]+r*Math.cos(l*Math.PI/2+theta+i*(Math.PI/2-2*theta)/32);
					  u[1]=center[1]+r*Math.sin(l*Math.PI/2+theta+i*(Math.PI/2-2*theta)/32);
					  fundamentalDomain(u);
					  v[0]=center[0]+r*Math.cos(l*Math.PI/2+theta+(i+1)*(Math.PI/2-2*theta)/32);
					  v[1]=center[1]+r*Math.sin(l*Math.PI/2+theta+(i+1)*(Math.PI/2-2*theta)/32);
					  fundamentalDomain(v);
					  int[] w1={0,0,0};
					  w1[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*u[1]))*Math.cos(2*Math.PI*u[0])/3);
					  w1[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*u[1]))*Math.sin(2*Math.PI*u[0])/3);
					  if (v[1]<=.5 && u[1]<=.5) {
					    int[] w2={0,0,0};
					    w2[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v[1]))*Math.cos(2*Math.PI*v[0])/3);
					    w2[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v[1]))*Math.sin(2*Math.PI*v[0])/3);
					    g.drawLine(w1[0]+fudge,w1[1],w2[0]+fudge,w2[1]);
					    if (l==0 && i==theta && getLabelShown())
						  g.drawString(displayText,w1[0]+fudge,w1[1]);
					  }
					  else g.drawOval(w1[0]+fudge,w1[1],1,1);
					}
				  }
				  break;
	  }
    }
  }
  public boolean mouseIsOver(double[] mouse, int SZ){
    double[] center={0,0,0}, point={0,0,0},u={0,0,0},v={0,0,0};
    int cs=GeoPlayground.getHght();
	double r=2,radius=2;
    constList.get(0).getXYZ(center);
    constList.get(1).getXYZ(point);
	for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
	  u[0]=point[0];
	  u[1]=point[1];
	  v[0]=center[0]+i;
	  v[1]=center[1]+j;
	  if (MathEqns.norm(u,v)<r) r=MathEqns.norm(u,v);
	  if (MathEqns.norm(v,mouse)<radius) radius=MathEqns.norm(v,mouse);
	}
	return Math.abs(cs*(radius-r))<3;
  }

  public void update() {
    setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
  }

  public double[] getCLxyz(ToroidalPoint inter, ToroidalConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
    constList.get(0).getXYZ(u);          // a is a circle w/ ctr A 
    constList.get(1).getXYZ(v);          //& pt B
    b.getXYZ(w);          // b is a line with slope w
    b.constList.get(1).getXYZ(z);        // and point z, we find a pt of intersection.
        
    inter.setValid(CircleEqns.calculateEucCL(u,v,w,z,x,(i==0)));
    return x;
  }


  public double[] getNewCLxyz(ToroidalPoint inter,ToroidalConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
    constList.get(0).getNewXYZ(u);          // a is a circle w/ ctr A & pt B
    constList.get(1).getNewXYZ(v);          // b is a line
    b.getNewXYZ(w);          // we find a pt of intersection.
    b.constList.get(1).getNewXYZ(z);
    
    inter.setValid(CircleEqns.calculateEucCL(u,v,w,z,x,(i==0)));
    return x;
  }

  public double[] getCCxyz(ToroidalPoint inter,ToroidalConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getXYZ(t);          // a is a circle
    constList.get(1).getXYZ(u);          // b is a circle
    b.get(0).getXYZ(v);       // we find a pt of intersection.
    b.get(1).getXYZ(w);
    inter.setValid(CircleEqns.calculateEucCC(t,u,v,w,x,(i==0)));
    return x;
  }
  public double[] getNewCCxyz(ToroidalPoint inter,ToroidalConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getNewXYZ(t);         // a is a circle
    constList.get(1).getNewXYZ(u);         // b is a circle
    b.get(0).getNewXYZ(v);      // we find a pt of intersection.
    b.get(1).getNewXYZ(w);
    inter.isRealNew=CircleEqns.calculateEucCC(t,u,v,w,x,(i==0));
    return x;
  }  
}

class ToroidalPTonLINE extends ToroidalPoint {
	public ToroidalPTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class ToroidalPTonCIRC extends ToroidalPoint {
	public ToroidalPTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}
class ToroidalMIDPT extends ToroidalPoint {
	public ToroidalMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] nm={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		resetSecondVector(v1,v2);
		nm[0]=(v1[0]+v2[0])/2;
		nm[1]=(v1[1]+v2[1])/2;
		nm[2]=0;
		fundamentalDomain(nm);
		setNewXYZ(nm);
	}
}

class ToroidalFIXedPT extends ToroidalPoint {
	public ToroidalFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}
class ToroidalTranslatePt extends ToroidalPoint {
	public ToroidalTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] v={0,0,0},w={0,0,0};
		EuclideanPoint a,b,c;
		constList.get(0).get(0).getNewXYZ(v);	a=new EuclideanPoint(POINT,tempList,v);
		constList.get(0).get(1).getNewXYZ(w);	
		resetSecondVector(v,w);					b=new EuclideanPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			c=new EuclideanPoint(POINT,tempList,w);
		tempList.add(a);	tempList.add(b);
		EuclideanLine d = new EuclideanLine(LINE,tempList);
		d.update();			d.getNewXYZ(w);		d.setXYZ(w);
		EuclideanMIDPT e = new EuclideanMIDPT(MIDPT,tempList,w);
		e.update();			e.getNewXYZ(w);		e.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(a);
		EuclideanPERP f = new EuclideanPERP(PERP,tempList);
		f.update();			f.getNewXYZ(w);		f.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(e);
		EuclideanPERP g = new EuclideanPERP(PERP,tempList);
		g.update();			g.getNewXYZ(w);		g.setXYZ(w);
		tempList.clear();	tempList.add(f);	tempList.add(c);
		EuclideanReflectPt h = new EuclideanReflectPt(REFLECT_PT,tempList,w);
		h.update();			h.getNewXYZ(w);		h.setXYZ(w);
		tempList.clear();	tempList.add(g);	tempList.add(h);
		EuclideanReflectPt i = new EuclideanReflectPt(REFLECT_PT,tempList,w);
		i.update();			i.getNewXYZ(w);	
		fundamentalDomain(w);
		setNewXYZ(w);
	}
}
class ToroidalMeasure extends ToroidalPoint {
    protected double[] a={0,0,0},b={0,0,0};
    double dist=2;
	public ToroidalMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
		super(t, clickedList, v);
		setDisplayText();
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	  if ((New && getValidNew()) || (!New && getValid())) {
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);
		}
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		dist=2;
		for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
			if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
				dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
		switch(GeoPlayground.model) {
	    case 0:
			g.fillRect(MathEqns.round(v1[0]*cs)-sz/2+1+fudge,MathEqns.round(v1[1]*cs)-sz/2+1,sz-2,sz-2);
			g.drawLine(MathEqns.round(v1[0]*cs)+fudge,MathEqns.round(v1[1]*cs),MathEqns.round(v1[0]*cs)+10+fudge,MathEqns.round(v1[1]*cs)-5);
		  break;
		case 1:
			g.fillRect(MathEqns.round(v1[0]*cs/3)-sz/2+1+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)-sz/2+1+2*SZ/3,sz-2,sz-2);
			g.drawLine(MathEqns.round(v1[0]*cs/3)+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3,
					   MathEqns.round(v1[0]*cs/3)+10+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)-5+2*SZ/3);
			  break;
		case 2:
		case 3:
			v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
			v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
		  if (v1[1]<=.5) {
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
			  if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
				dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			g.fillRect(MathEqns.round(v2[0])-sz/2+1+fudge,MathEqns.round(v2[1])-sz/2+1,sz-2,sz-2);
			g.drawLine(MathEqns.round(v2[0])+fudge,MathEqns.round(v2[1]),MathEqns.round(v2[0])+10+fudge,MathEqns.round(v2[1])-5);
		  }
			break;
	  }
	 }
	}
}
class ToroidalCOMMENT extends ToroidalPoint {
	protected double[] a={0,0,0},b={0,0,0};
    double dist=2;
    public ToroidalCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if ((New && getValidNew()) || (!New && getValid())) {
			switch(GeoPlayground.model) {
		    case 0:
				g.fillRect(MathEqns.round(v1[0]*cs)-sz/2+1+fudge,MathEqns.round(v1[1]*cs)-sz/2+1,sz-2,sz-2);
				g.drawLine(MathEqns.round(v1[0]*cs)+fudge,MathEqns.round(v1[1]*cs),MathEqns.round(v1[0]*cs)+10+fudge,MathEqns.round(v1[1]*cs)-5);
				g.drawString(displayText,MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
			  break;
			case 1:
				for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
				g.fillRect(MathEqns.round(v1[0]*cs/3)-sz/2+1+fudge+2*SZ/3+i*2*SZ/3,MathEqns.round(v1[1]*cs/3)-sz/2+1+2*SZ/3+j*2*SZ/3,sz-2,sz-2);
				g.drawLine(MathEqns.round(v1[0]*cs/3)+fudge+2*SZ/3+i*2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3+j*2*SZ/3,
						   MathEqns.round(v1[0]*cs/3)+10+fudge+2*SZ/3+i*2*SZ/3,MathEqns.round(v1[1]*cs/3)-5+2*SZ/3+j*2*SZ/3);
				g.drawString(displayText,MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3+i*2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3+j*2*SZ/3);
				}
				break;
			case 2:
			case 3:
				v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
				v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
			  if (v1[1]<=.5) {
				for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				  if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
				g.fillRect(MathEqns.round(v2[0])-sz/2+1+fudge,MathEqns.round(v2[1])-sz/2+1,sz-2,sz-2);
				g.drawLine(MathEqns.round(v2[0])+fudge,MathEqns.round(v2[1]),
						   MathEqns.round(v2[0])+10+fudge,MathEqns.round(v2[1])-5);
				g.drawString(displayText,MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
			  }
				break;
		  }
		 }
	}
}
class ToroidalCONSTANT extends ToroidalPoint {
	public ToroidalCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	  if ((New && getValidNew()) || (!New && getValid())) {
		switch(GeoPlayground.model) {
	    case 0:
			g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
			break;
		case 1:
			g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),
					MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
			break;
		case 2:
		case 3:
			v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
			v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
		  if (v1[1]<=.5) {
			g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
		  }
		  break;
	  }
	 }
	}

}
class ToroidalSUM extends ToroidalMeasure {
	public ToroidalSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if (!New) {
				if (getType()==SUM)		measureValue=get(0).measureValue+get(1).measureValue;
				if (getType()==DIFF)	measureValue=get(0).measureValue-get(1).measureValue;
				if (getType()==PROD)	measureValue=get(0).measureValue*get(1).measureValue;
				switch(GeoPlayground.model) {
				case 0:
				    g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
					break;
				case 1:
					g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),
							MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
					break;
				case 2:
				case 3:
					v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
					v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
					if (v1[1]<=.5) {
						for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
						  if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
							dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
						g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
					 }
					break;
				}
			}
			else {
				if (getType()==SUM)		measureValueNew=get(0).measureValueNew+get(1).measureValueNew;
				if (getType()==DIFF)	measureValueNew=get(0).measureValueNew-get(1).measureValueNew;
				if (getType()==PROD)	measureValueNew=get(0).measureValueNew*get(1).measureValueNew;
				switch(GeoPlayground.model) {
				case 0:
				    g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13,MathEqns.round(v1[1]*cs));
					break;
				case 1:
					g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),
							MathEqns.round(v1[0]*cs/3)+13+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
					break;
				case 2:
				case 3:
					v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
					v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
					if (v1[1]<=.5) {
						for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
						  if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
							dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
						g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
					}
					break;
				}
			}
		}
	}
	public void update() {
		super.update();
		for (int i=0;i<2;i++) get(i).update();
		if (getType()==SUM)		measureValue=get(0).measureValue+get(1).measureValue;
		if (getType()==DIFF)	measureValue=get(0).measureValue-get(1).measureValue;
		if (getType()==PROD)	measureValue=get(0).measureValue*get(1).measureValue;
		if (getType()==SUM)		measureValueNew=get(0).measureValueNew+get(1).measureValueNew;
		if (getType()==DIFF)	measureValueNew=get(0).measureValueNew-get(1).measureValueNew;
		if (getType()==PROD)	measureValueNew=get(0).measureValueNew*get(1).measureValueNew;
		
	}
}
class ToroidalRATIO extends ToroidalMeasure {
	public ToroidalRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if (!New) {
				if (get(1).measureValue!=0) {
					measureValue=get(0).measureValue/get(1).measureValue;
					switch(GeoPlayground.model) {
				    case 0:
				    	g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
					  break;
					case 1:
						g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),
								MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
						  break;
					case 2:
					case 3:
						v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
						v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
					  if (v1[1]<=.5) {
						for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
						  if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
							dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
						g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
					  }
					  break;
					}
				}
			}
			else {
				if (get(1).measureValueNew!=0) {
					measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
					switch(GeoPlayground.model) {
				    case 0:
				    	g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
					  break;
					case 1:
						g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),
								MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
						  break;
					case 2:
					case 3:
						v2[0]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3;
						v2[1]=SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3;
					  if (v1[1]<=.5) {
						for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
						  if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
							dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
						g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
					  }
					  break;
					}
				}
			}
		}
	}
	public void update() {
		super.update();
		for (int i=0;i<2;i++) get(i).update();
		if (get(1).measureValue==0)		get(1).measureValue=.00000001;
		if (get(1).measureValueNew==0)	get(1).measureValueNew=.00000001;
		measureValue=get(0).measureValue/get(1).measureValue;
		measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
	}
}
class ToroidalDISTANCE extends ToroidalMeasure {
	public ToroidalDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		switch (GeoPlayground.model) {
			case 0:
				g.drawString(displayText+"\u2248"
					+MathEqns.chop(dist,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
				break;
			case 1:
				g.drawString(displayText+"\u2248"+MathEqns.chop(dist,GeoPlayground.digits),
						MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
					break;
			case 2:
			case 3:
				if (v1[1]<=.5)
					g.drawString(displayText+"\u2248"
							+MathEqns.chop(dist,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
				break;
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			dist=2;
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			measureValueNew=dist;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			dist=2;
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			measureValue=dist;
		}
	}
}
class ToroidalTRIANGLE extends ToroidalMeasure {
	public ToroidalTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		double[] c={0,0,0};
		double ss,ab,ac,bc;
		if (New) get(2).getNewXYZ(c);
		else get(2).getXYZ(c);
		ab=MathEqns.norm(a,b);	ac=MathEqns.norm(a,c);	bc=MathEqns.norm(b,c);
		ss=(ab+ac+bc)/2;
		ToroidalANGLE aa,bb,cc;
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(get(0));	tempList.add(get(1));	tempList.add(get(2));
		aa=new ToroidalANGLE(ANGLE,tempList,a);	aa.measureValue=1; aa.update(); aa.getNewXYZ(a);	aa.setXYZ(a);
		tempList.clear();
		tempList.add(get(1));	tempList.add(get(2));	tempList.add(get(0));
		bb=new ToroidalANGLE(ANGLE,tempList,b);	bb.measureValue=1; bb.update(); bb.getNewXYZ(a);	bb.setXYZ(a);
		tempList.clear();
		tempList.add(get(2));	tempList.add(get(0));	tempList.add(get(1));
		cc=new ToroidalANGLE(ANGLE,tempList,c);	cc.measureValue=1; cc.update(); cc.getNewXYZ(a);	cc.setXYZ(a);
		double temp;
		double angleSum=aa.measureValue+bb.measureValue+cc.measureValue;
		if ((GeoPlayground.degrees && angleSum<181) || (!GeoPlayground.degrees && angleSum<Math.PI+.0001))
			temp=Math.sqrt(ss*(ss-ab)*(ss-ac)*(ss-bc));
		else temp=1e-17;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (GeoPlayground.model==0)
		g.drawString(displayText+"\u2248"
				   +MathEqns.chop(temp,GeoPlayground.digits),(int)(cs*v1[0])+13+fudge,(int)(cs*v1[1]));
		else if (GeoPlayground.model==1)
			g.drawString(displayText+"\u2248"
					   +MathEqns.chop(temp,GeoPlayground.digits),(int)(cs*v1[0]/3)+13+fudge+2*SZ/3,(int)(cs*v1[1]/3)+2*SZ/3);
		else {
			if (v1[1]<=.5) {
				if (New) get(2).getNewXYZ(c);
				else get(2).getXYZ(c);
				resetSecondVector(b,a);
				resetSecondVector(b,c);
				g.drawString(displayText+"\u2248"
					   +MathEqns.chop(temp,GeoPlayground.digits),
					   MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
			}
		}
		}
	}
	public void update() {
		super.update();
		double[] c={0,0,0};
		double ss,ab,ac,bc;
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			ab=MathEqns.norm(a,b);	ac=MathEqns.norm(a,c);	bc=MathEqns.norm(b,c);
			ss=(ab+ac+bc)/2;
			ToroidalANGLE aa,bb,cc;
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			tempList.add(get(0));	tempList.add(get(1));	tempList.add(get(2));
			aa=new ToroidalANGLE(ANGLE,tempList,a);	aa.measureValue=1; aa.update(); aa.getNewXYZ(a);	aa.setXYZ(a);
			tempList.clear();
			tempList.add(get(1));	tempList.add(get(2));	tempList.add(get(0));
			bb=new ToroidalANGLE(ANGLE,tempList,b);	bb.measureValue=1; bb.update(); bb.getNewXYZ(a);	bb.setXYZ(a);
			tempList.clear();
			tempList.add(get(2));	tempList.add(get(0));	tempList.add(get(1));
			cc=new ToroidalANGLE(ANGLE,tempList,c);	cc.measureValue=1; cc.update(); cc.getNewXYZ(a);	cc.setXYZ(a);
			double temp;
			double angleSum=aa.measureValue+bb.measureValue+cc.measureValue;
			if ((GeoPlayground.degrees && angleSum<181) || (!GeoPlayground.degrees && angleSum<Math.PI+.0001))
				temp=Math.sqrt(ss*(ss-ab)*(ss-ac)*(ss-bc));
			else temp=1e-17;
			measureValueNew=temp;
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			ab=MathEqns.norm(a,b);	ac=MathEqns.norm(a,c);	bc=MathEqns.norm(b,c);
			ss=(ab+ac+bc)/2;
			tempList.clear();
			tempList.add(get(0));	tempList.add(get(1));	tempList.add(get(2));
			aa=new ToroidalANGLE(ANGLE,tempList,a);	aa.measureValue=1; aa.update(); aa.getNewXYZ(a);	aa.setXYZ(a);
			tempList.clear();
			tempList.add(get(1));	tempList.add(get(2));	tempList.add(get(0));
			bb=new ToroidalANGLE(ANGLE,tempList,b);	bb.measureValue=1; bb.update(); bb.getNewXYZ(a);	bb.setXYZ(a);
			tempList.clear();
			tempList.add(get(2));	tempList.add(get(0));	tempList.add(get(1));
			cc=new ToroidalANGLE(ANGLE,tempList,c);	cc.measureValue=1; cc.update(); cc.getNewXYZ(a);	cc.setXYZ(a);
			angleSum=aa.measureValue+bb.measureValue+cc.measureValue;
			if ((GeoPlayground.degrees && angleSum<181) || (!GeoPlayground.degrees && angleSum<Math.PI+.0001))
				temp=Math.sqrt(ss*(ss-ab)*(ss-ac)*(ss-bc));
			else temp=1e-17;
			measureValue=temp;
		}
	}
}
class ToroidalANGLE extends ToroidalMeasure {
	public ToroidalANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		double[] c={0,0,0};
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		switch (GeoPlayground.model) {
			case 0:
			case 1:
				if (New) {
					get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
				}
				else {
					get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
				}
				resetSecondVector(b,a);
				resetSecondVector(b,c);
				double csn=cs; int temp=0;
				if (GeoPlayground.model>0) {csn/=3;temp=1;}
				if (GeoPlayground.degrees)
					g.drawString(displayText+"\u2248"
						   +MathEqns.chop(MathEqns.eucAngle(a,b,c),GeoPlayground.digits)+"\u00b0",
						   (int)(csn*v1[0])+13+fudge+2*SZ/3*temp,(int)(csn*v1[1])+2*SZ/3+temp);
				else
					g.drawString(displayText+"\u2248"
							   +MathEqns.chop(MathEqns.eucAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits),
							   (int)(csn*v1[0])+13+fudge+2*SZ/3*temp,(int)(csn*v1[1])+2*SZ/3*temp);
				break;
			case 2:
			case 3:
				if (v1[1]<=.5) {
					if (New) get(2).getNewXYZ(c);
					else get(2).getXYZ(c);
					resetSecondVector(b,a);
					resetSecondVector(b,c);
					if (GeoPlayground.degrees)
						g.drawString(displayText+"\u2248"
						   +MathEqns.chop(MathEqns.eucAngle(a,b,c),GeoPlayground.digits)+"\u00b0",
						   MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
					else
						g.drawString(displayText+"\u2248"
								   +MathEqns.chop(MathEqns.eucAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits),
								   MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
				}
				break;
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0};
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			resetSecondVector(b,a);
			resetSecondVector(b,c);		
			measureValueNew=MathEqns.eucAngle(a,b,c);
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			resetSecondVector(b,a);
			resetSecondVector(b,c);		
			measureValue=MathEqns.eucAngle(a,b,c);
			if (!GeoPlayground.degrees) {
				measureValueNew=measureValueNew/180.*Math.PI;
				measureValue=measureValue/180.*Math.PI;
			}
		}
	}
}

class ToroidalCIRCUMF extends ToroidalMeasure {
	public ToroidalCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		double circ=2*dist;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		switch (GeoPlayground.model){
			case 0:
				if (dist>.5) circ*=(1-4/Math.PI*Math.acos(1/(2*dist)));
				g.drawString(displayText+"\u2248"
					   +MathEqns.chop(circ*Math.PI,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
				break;
			case 1:
				if (dist>.5) circ*=(1-4/Math.PI*Math.acos(1/(2*dist)));
				g.drawString(displayText+"\u2248"
					   +MathEqns.chop(circ*Math.PI,GeoPlayground.digits),
					   MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
				break;
			case 2:
			case 3:
				if (dist>.5) circ*=(1-4/Math.PI*Math.acos(1/(2*dist)));
				if (v1[1]<=.5)
					g.drawString(displayText+"\u2248"
						   +MathEqns.chop(circ*Math.PI,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
				
				break;
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			dist=2;
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			measureValueNew=2*dist*Math.PI;
			if (dist>.5) measureValueNew*=(1-4/Math.PI*Math.acos(1/(2*dist)));
			get(0).getXYZ(a);		get(1).getXYZ(b);
			dist=2;
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			measureValue=2*dist*Math.PI;
			if (dist>.5) measureValue*=(1-4/Math.PI*Math.acos(1/(2*dist)));
			
		}
	}
}

class ToroidalAREA extends ToroidalMeasure {
	public ToroidalAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		double area=dist*dist;
		if (dist>.5) area=area*(1-4/Math.PI*Math.acos(1/(2*dist)))+Math.sqrt(4*dist*dist-1)/Math.PI;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		switch (GeoPlayground.model){
			case 0:
				g.drawString(displayText+"\u2248"
					   +MathEqns.chop(area*Math.PI,GeoPlayground.digits),MathEqns.round(v1[0]*cs)+13+fudge,MathEqns.round(v1[1]*cs));
				break;
			case 1:
				g.drawString(displayText+"\u2248"
						   +MathEqns.chop(area*Math.PI,GeoPlayground.digits),
						   MathEqns.round(v1[0]*cs/3)+13+fudge+2*SZ/3,MathEqns.round(v1[1]*cs/3)+2*SZ/3);
					break;
			case 2:
			case 3:
				if (v1[1]<=.5)
					g.drawString(displayText+"\u2248"
					   +MathEqns.chop(area*Math.PI,GeoPlayground.digits),MathEqns.round(v2[0])+13+fudge,MathEqns.round(v2[1]));
				break;
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			dist=2;
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			double area=dist*dist*Math.PI;
			if (dist>.5) area=area*(1-4/Math.PI*Math.acos(1/(2*dist)))+Math.sqrt(4*dist*dist-1)/Math.PI;
			measureValueNew=area;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			dist=2;
			for (int i=-1;i<2;i++) for (int j=-1;j<2;j++)
				if (Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j))<dist)
					dist = Math.sqrt((a[0]-b[0]+i)*(a[0]-b[0]+i)+(a[1]-b[1]+j)*(a[1]-b[1]+j));
			area=dist*dist*Math.PI;
			if (dist>.5) area=area*(1-4/Math.PI*Math.acos(1/(2*dist)))+Math.sqrt(4*dist*dist-1)/Math.PI;
			measureValue=area;
			
		}
	}
}

class ToroidalPERP extends ToroidalLine {
	public ToroidalPERP(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		if(getValidNew()) {
			v2[0]=-v1[1];
			v2[1]=v1[0];
			setNewXYZ(v2);
		}  
	}
}
class ToroidalPARLL0 extends ToroidalLine {
	public ToroidalPARLL0(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		if(getValidNew()) {
			v2[0]=v1[0];
			v2[1]=v1[1];
			setNewXYZ(v2);
		}  
	}
}

class ToroidalSEGMENT extends ToroidalLine {
	public ToroidalSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		for (int i=0;i<20;i++) {
			  v1[0]=vec1[0]*i/20+vec2[0]*(20-i)/20;
			  v1[1]=vec1[1]*i/20+vec2[1]*(20-i)/20;
			  v2[0]=vec1[0]*(i+1)/20+vec2[0]*(19-i)/20;
			  v2[1]=vec1[1]*(i+1)/20+vec2[1]*(19-i)/20;
			  fundamentalDomain(v1);
			  fundamentalDomain(v2);
			  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			  switch(GeoPlayground.model) {
		        case 0:
		        	if (MathEqns.norm(v1,v2)<.5)
		        		g.drawLine((int)(v1[0]*cs)+fudge,(int)(v1[1]*cs),(int)(v2[0]*cs)+fudge,(int)(v2[1]*cs));
		        	if (i==7 && getLabelShown())
		        		g.drawString(displayText,(int)(v1[0]*cs)+fudge,(int)(v1[1]*cs));
				  break;
			    case 1:
					  for (int m=0;m<3;m++) for (int n=0;n<3;n++) {
					  if (MathEqns.norm(v1,v2)<.5)
					    g.drawLine((int)(v1[0]*cs/3)+fudge+2*SZ/3*m,(int)(v1[1]*cs/3)+2*SZ/3*n,
					    		(int)(v2[0]*cs/3)+fudge+2*SZ/3*m,(int)(v2[1]*cs/3)+2*SZ/3*n);
					  if (i==7 && getLabelShown() && m==1 && n==1)
					    g.drawString(displayText,(int)(v1[0]*cs/3)+fudge+2*SZ/3,(int)(v1[1]*cs/3)+2*SZ/3);
					  }
					  break;
			    case 2:
			    case 3:
			    	int[] w1={0,0,0};
					  w1[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.cos(2*Math.PI*v1[0])/3);
					  w1[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v1[1]))*Math.sin(2*Math.PI*v1[0])/3);
					  if (v2[1]<=.5 && v1[1]<=.5) {
				        int[] w2={0,0,0};
					    w2[0]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v2[1]))*Math.cos(2*Math.PI*v2[0])/3);
					    w2[1]=MathEqns.round(SZ+SZ*(2+Math.cos(2*Math.PI*v2[1]))*Math.sin(2*Math.PI*v2[0])/3);
					    g.drawLine(w1[0]+fudge,w1[1],w2[0]+fudge,w2[1]);
				        if (i==7 && getLabelShown())
				          g.drawString(displayText,w1[0]+fudge,w1[1]);
					  }
					  else g.drawOval(w1[0]+fudge,w1[1],1,1);
				      break;
			  }
			}
	}
	public void update() {
		super.update();
	}
}

class ToroidalBISECTOR extends ToroidalLine {

	public ToroidalBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		if (getValidNew()) {
			double[] aa={0,0,0},bb={0,0,0},cc={0,0,0};
			constList.get(0).getNewXYZ(aa);
			constList.get(1).getNewXYZ(bb);
			constList.get(2).getNewXYZ(cc);
			resetSecondVector(bb,aa);
			resetSecondVector(bb,cc);
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			EuclideanPoint a=new EuclideanPoint(POINT,tempList,aa);
			EuclideanPoint b=new EuclideanPoint(POINT,tempList,bb);
			EuclideanPoint c=new EuclideanPoint(POINT,tempList,cc);
			tempList.add(b);	tempList.add(c);
			EuclideanLine d=new EuclideanLine(LINE,tempList);
			d.update();	d.getNewXYZ(v1);
			tempList.clear();	tempList.add(b);	tempList.add(a);
			EuclideanCircle e=new EuclideanCircle(CIRCLE,tempList);
			EuclideanPoint f1,f2;
			f1=((EuclideanLine)d).intersect(0,(EuclideanCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((EuclideanLine)d).intersect(1,(EuclideanCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(f1);
			EuclideanPoint g1=new EuclideanMIDPT(MIDPT,tempList,v1);
			g1.update();	g1.getNewXYZ(v1);	g1.setXYZ(v1);
			tempList.clear();	tempList.add(a);	tempList.add(f2);
			EuclideanPoint g2=new EuclideanMIDPT(MIDPT,tempList,v2);
			g2.update();	g2.getNewXYZ(v2);	g2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(c);
			EuclideanPoint g3=new EuclideanMIDPT(MIDPT,tempList,bb);
			g3.update();	g3.getNewXYZ(bb);	g3.setXYZ(bb);
			if (MathEqns.norm(v1,bb)>MathEqns.norm(v2,bb)) {
				g1=g2;
			}
			tempList.clear();	tempList.add(b);	tempList.add(g1);
			EuclideanLine h=new EuclideanLine(LINE,tempList);
			h.update();	h.getNewXYZ(v1);
			if (MathEqns.norm(v1)<=.0001) {
				tempList.clear();	tempList.add(a);	tempList.add(c);
				d=new EuclideanLine(LINE,tempList);
				d.update();			d.getNewXYZ(v1);	d.setXYZ(v1);
				tempList.clear();	tempList.add(d);	tempList.add(b);
				h=new EuclideanPERP(PERP,tempList);
				h.update();	h.getNewXYZ(v1);
			}
			for (int i=0;i<3;i++) v1[i]*=-1;
			if (MathEqns.norm(v1)>.0001) setNewXYZ(v1);
			else setValidNew(false);
		}
	}
}
