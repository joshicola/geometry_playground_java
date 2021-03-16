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

import java.awt.Graphics;
import java.util.LinkedList;

abstract class ConicalConstruct extends GeoConstruct {
	protected double[] v1={0,0,0},v2={0,0,0};
  protected static int scale=3;// used for cone angle
  public ConicalConstruct(int t, LinkedList<GeoConstruct> clickedList){
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
  public ConicalConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v){
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
	  }
	  if (t==DISTANCE || t==RATIO || t==SUM || t==DIFF || t==PROD) {
		double[] u={0,0,0};
		constList.get(0).getXYZ(v);
		constList.get(1).getXYZ(u);
		resetSecondVector(v,u);
		v[0]=(v[0]+u[0])/2;
		v[1]=(v[1]+u[1])/2;
	  }
	  if (t==TRIANGLE) {
		double[] u={0,0,0},w={0,0,0};
		constList.get(0).getXYZ(v);
		constList.get(1).getXYZ(u);
		constList.get(2).getXYZ(w);
		resetSecondVector(v,u);
		resetSecondVector(v,w);
		v[0]=(u[0]+w[0]+v[0])/3;
		v[1]=(u[1]+w[1]+v[1])/3;
	  }
	  if (t==CONSTANT) {
		measureValue=v[2];
		measureValueNew=v[2];
		v[2]=0;
	  }
	  setXYZ(v);
	  update();
  }
  public abstract ConicalPoint intersect(int m, ConicalConstruct a);

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
    double[] center={0,0,0};
    LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
    EuclideanPoint c=new EuclideanPoint(POINT,temp,center);
    EuclideanPoint t=new EuclideanPoint(POINT,temp,v);
    MathEqns.transform(c,t,dragStart,dragNow);
    t.getNewXYZ(v);
	this.setNewXYZ(v);
  }
  public static double getScale() {return (double)(360/scale);}
  public static void setScale(double s){
	  if (s<0 && scale>2) scale--;
	  else if (s>0 && scale<6) scale++;
  }
  public static void resetScale(){scale=3;}
  public static void rescale(double[] v){}
  public static void unscale(double[] v){}
  public void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow) {
    MathEqns.transform(fixedObject,this,dragStart,dragNow);
  }
  public static double arctan(double x,double y){
	  if (x==0) return Math.PI/2;
	  if (x>0) return Math.atan(y/x);
	  if (y>=0) return Math.atan(y/x)+Math.PI;
	  return Math.atan(y/x)-Math.PI;
  }
  public void resetSecondVector(double[] u, double[] v) {
	  double[]	w = {0,0,0};
	  double	dist=MathEqns.norm(u,v),
	  			rad=MathEqns.norm(v),
	  			arg=arctan(v[1],v[0]);
	  int h=0;
	  for (int i=-1;i<2;i++) {
		  w[0]=rad*Math.sin(arg+2*i*Math.PI/scale);
		  w[1]=rad*Math.cos(arg+2*i*Math.PI/scale);
		  if (MathEqns.norm(u,w)<dist) {
			dist=MathEqns.norm(u,w);
			h=i;
		  }
	  }
	  v[0]=rad*Math.sin(arg+2*h*Math.PI/scale);
	  v[1]=rad*Math.cos(arg+2*h*Math.PI/scale);
  }
} // end class 


class ConicalPoint extends ConicalConstruct{
	protected int sz;
  public ConicalPoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}


  public ConicalPoint intersect(int m, ConicalConstruct a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	ConicalPoint newPoint=new ConicalPoint(LINEintLINE0,tempList,v1);
    return newPoint;
  }

  public void draw(Graphics g, int SZ, boolean New) {
    sz=(int)MathEqns.max(4,SZ/40);
    if ((New && isRealNew) || (!New && isReal)) {
      int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
  	  if(New){
        getNewXYZ(v1);
      }
      else{
        getXYZ(v1);
      }
  	  double[]	w = {0,0,0};
  	  double	rad=MathEqns.norm(v1),
  	  			arg=arctan(v1[1],v1[0]);
	  if (getType()<30)
	  switch(GeoPlayground.model) {
	    case 0:
	    	v1[0]=SZ+fudge+rad*Math.sin(scale*arg);
	    	v1[1]=SZ+rad*Math.cos(scale*arg);
		    g.fillOval((int)v1[0]-sz/2,(int)v1[1]-sz/2,sz,sz);
		    if (getLabelShown())
		      g.drawString(displayText,(int)v1[0]-sz/2,(int)v1[1]-sz/2);
		  break;
		case 1:
			sz=(int)MathEqns.max(sz-2,2);
			for (int i=0;i<scale;i++) {
				w[0]=SZ+fudge+rad*Math.sin(arg+2*i*Math.PI/scale);
				w[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.fillOval((int)(w[0]-sz/2),(int)(w[1])-sz/2,sz,sz);
				if (getLabelShown())
					g.drawString(displayText,(int)(w[0]-sz/2),(int)(w[1])-sz/2);
			}
			break;
		case 2:
		case 3:
			v1[0]=SZ+fudge+2*rad/scale*Math.sin(scale*arg);
			v1[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
			v1[2]=2*rad/scale*Math.cos(scale*arg);
			if (v1[2]<0) {
				sz=(int)MathEqns.max(sz-2,2);
				g.fillOval((int)v1[0],(int)v1[1],sz/2,sz/2);
			}
			else {
				  g.fillOval((int)v1[0]-sz/2,(int)v1[1]-sz/2,sz,sz);
			      if (getLabelShown())
			        g.drawString(displayText,(int)v1[0]-sz/2,(int)v1[1]-sz/2);
			}
			break;
	  }
    }
  }

  public boolean mouseIsOver(double[] v, int SZ){
    double sz=MathEqns.max(6,SZ/20);
    double rad=MathEqns.norm(v);
    double arg=arctan(v[1],v[0]);
    double[] w={0,0,0};
    boolean isOnIt=false;
    for (int i=0;i<scale;i++) {
    	w[0]=rad*Math.sin(arg+Math.PI*2*i/scale);
    	w[1]=rad*Math.cos(arg+Math.PI*2*i/scale);
    	if ((x-w[0])*(x-w[0])+(y-w[1])*(y-w[1])<(sz*sz)) isOnIt=true;
    }
	return isOnIt;
  }

  public void updatePTonLINE(){
	  double[] v2={0,0,0},v1={0,0,0},r={0,0,0},v0={newX,newY,newZ};
	  int tempI=0;
	    constList.get(0).get(1).getNewXYZ(v2);
		if (constList.get(0).getType()==PERP || constList.get(0).getType()==BISECTOR) {
		  v1[0]=v2[0]+(constList.get(0).newX/2);
		  v1[1]=v2[1]+(constList.get(0).newY/2);
		  v1[2]=0;
		  v2[0]-=(constList.get(0).newX/2);
		  v2[1]-=(constList.get(0).newY/2);
		}
		else {//(type==LINE || type==SEGMENT || type==RAY)
		  constList.get(0).get(0).getNewXYZ(v1);
		  resetSecondVector(v1,v2);
		}
		double dist=1e8;
		int k=1100; if (constList.get(0).getType()==RAY ||
						constList.get(0).getType()==SEGMENT) k=98;
		int j=-1000; if(constList.get(0).getType()==SEGMENT) j=2;
		for (int i=j;i<=k;i++) {
		  r[0]=v1[0]*i/100+v2[0]*(100-i)/100;
		  r[1]=v1[1]*i/100+v2[1]*(100-i)/100;
		  double arg=arctan(r[1],r[0]);
		  double rad=Math.sqrt(r[0]*r[0]+r[1]*r[1]);
		  for (int m=0;m<scale;m++) {
			  r[0]=rad*Math.sin(arg+m*2*Math.PI/scale);
			  r[1]=rad*Math.cos(arg+m*2*Math.PI/scale);
			  if (MathEqns.norm(r,v0)<dist) {dist=MathEqns.norm(r,v0);tempI=i;}
		  }
		}
		r[0]=v1[0]*tempI/100+v2[0]*(100-tempI)/100;
		r[1]=v1[1]*tempI/100+v2[1]*(100-tempI)/100;
		r[2]=0;
		setNewXYZ(r);
  }
  public void updatePTonCIRC() {
	  double[] center={0,0,0},radial={0,0,0},u={0,0,0},w={0,0,0},v0={newX,newY,newZ};;
	  double dist=1e8;
	  int tempI=0;
      constList.get(0).get(0).getNewXYZ(center);
      constList.get(0).get(1).getNewXYZ(radial);
      resetSecondVector(center,radial);
	  double rad=MathEqns.norm(center,radial);
	  for (int i=-355;i<355;i++){// 355/113. is slightly larger than pi
		u[0]=center[0]+rad*Math.sin(i/113.);
		u[1]=center[1]+rad*Math.cos(i/113.);
		w[0]=u[0];	w[1]=u[1];
		resetSecondVector(center,w);
		if (MathEqns.norm(center,radial)<MathEqns.norm(center,w)+.01)
			if (MathEqns.norm(u,v0)<dist) {
				dist=MathEqns.norm(u,v0);
				tempI=i;
			}
	  }
	  u[0]=center[0]+rad*Math.sin(tempI/113.);
	  u[1]=center[1]+rad*Math.cos(tempI/113.);
	  u[2]=0;
	  setNewXYZ(u);
  }
  public void update() {
	boolean nowValid=true;
	for (int i=0;i<constList.size();i++) nowValid = (nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
  }
}

class ConicalLine extends ConicalConstruct{
  double[] vec1={0,0,0}, vec2={0,0,0};
  int cs;
  public ConicalLine(int t, LinkedList<GeoConstruct> clickedList){
    super(t,clickedList);
  }
  public ConicalPoint intersect(int m, ConicalConstruct a){
    ConicalPoint newPoint;
    if(a.getType()==0)
      newPoint=intersect(m,(ConicalCircle)a);
    else
      newPoint=intersect(m,(ConicalLine)a);
    return newPoint;
  }
  public ConicalPoint intersect(int m, ConicalLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	ConicalPoint newPoint=new ConicalPoint(LINEintLINE0,tempList,v1);
    return newPoint;	// intersections not implemented in Conical
  }
  public ConicalPoint intersect(int m, ConicalCircle a){
    ConicalPoint newPoint=a.intersect(m,this);
    return newPoint;	// intersections not implemented in Conical
  }
  
  public void draw(Graphics g, int SZ, boolean New) {
    if (isReal) {
      double[] a={0,0,0},u={0,0,0},v={0,0,0};
      int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
      if(New){
    	update();
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
	  if (type==LINE || type==RAY || type==SEGMENT) {
		  a[0]=vec2[0]-vec1[0];
		  a[1]=vec2[1]-vec1[1];
	  }
	  if (type!=SEGMENT) {
			int m,k;
			double rad=0,arg=0;
			switch(GeoPlayground.model){
				case 0:
					MathEqns.normalize(a);
					k=-1500; m=1; if (type==RAY) {k=0;m=-1;}
					if (MathEqns.norm(vec1,vec2)>.00001) for (int i=k;i<1500;i+=2) {
						u[0]=vec2[0]+i*a[0];
						u[1]=vec2[1]+i*a[1];
						v[0]=vec2[0]+(i+2)*a[0];
						v[1]=vec2[1]+(i+2)*a[1];
						if (type==RAY) {
							u[0]+=(vec1[0]-vec2[0]); u[1]+=(vec1[1]-vec2[1]);
							v[0]+=(vec1[0]-vec2[0]); v[1]+=(vec1[1]-vec2[1]);
						}
					rad=Math.sqrt(u[0]*u[0]+u[1]*u[1]);
					arg=scale*arctan(u[1],u[0]);
					u[0]=rad*Math.sin(arg);
					u[1]=rad*Math.cos(arg);
					rad=Math.sqrt(v[0]*v[0]+v[1]*v[1]);
					arg=scale*arctan(v[1],v[0]);
					v[0]=rad*Math.sin(arg);
					v[1]=rad*Math.cos(arg);
					g.drawLine(SZ+(int)(u[0])+fudge,SZ+(int)(u[1]),SZ+(int)(v[0])+fudge,SZ+(int)(v[1]));
					if (getLabelShown() && i==-50*m)
						g.drawString(displayText,SZ+(int)(u[0])+fudge,SZ+(int)(u[1]));
					}
					break;
				case 1:
					double[] w={0,0,0},z={0,0,0};
					k=-10; m=-1; if (type==RAY) {k=0;m=1;}
					if (MathEqns.norm(vec1,vec2)>.00001) for (int i=k;i<10;i++) {
						u[0]=vec2[0]+20*Math.tan(i/7.)*a[0];
						u[1]=vec2[1]+20*Math.tan(i/7.)*a[1];
						v[0]=vec2[0]+20*Math.tan((i+1)/7.)*a[0];
						v[1]=vec2[1]+20*Math.tan((i+1)/7.)*a[1];
						if (type==RAY) {
							u[0]+=(vec1[0]-vec2[0]); u[1]+=(vec1[1]-vec2[1]);
							v[0]+=(vec1[0]-vec2[0]); v[1]+=(vec1[1]-vec2[1]);
						}
						for (int j=0;j<scale;j++) {
						w[0]=Math.cos(2*Math.PI*j/scale)*u[0]+Math.sin(2*Math.PI*j/scale)*u[1];
						w[1]=-Math.sin(2*Math.PI*j/scale)*u[0]+Math.cos(2*Math.PI*j/scale)*u[1];
						z[0]=Math.cos(2*Math.PI*j/scale)*v[0]+Math.sin(2*Math.PI*j/scale)*v[1];
						z[1]=-Math.sin(2*Math.PI*j/scale)*v[0]+Math.cos(2*Math.PI*j/scale)*v[1];
						g.drawLine(SZ+(int)(w[0])+fudge,SZ+(int)(w[1]),SZ+(int)(z[0])+fudge,SZ+(int)(z[1]));
						if (getLabelShown() && i==0)
							g.drawString(displayText,
									SZ+(int)(.97*w[0]+.03*m*z[0])+fudge,SZ+(int)(.97*w[1]+.03*m*z[1]));
						}
					}
					break;
				case 2:
				case 3:
					MathEqns.normalize(a);
					k=-1500; m=-1; if (type==RAY) {k=0;m=1;}
					if (MathEqns.norm(vec1,vec2)>.00001) for (int i=k;i<1500;i+=3) {
						u[0]=vec2[0]+i*a[0];
						u[1]=vec2[1]+i*a[1];
						v[0]=vec2[0]+(i+3)*a[0];
						v[1]=vec2[1]+(i+3)*a[1];
						if (type==RAY) {
							u[0]+=(vec1[0]-vec2[0]); u[1]+=(vec1[1]-vec2[1]);
							v[0]+=(vec1[0]-vec2[0]); v[1]+=(vec1[1]-vec2[1]);
						}
						rad=Math.sqrt(u[0]*u[0]+u[1]*u[1]);
					arg=scale*arctan(u[1],u[0]);
					u[0]=2*rad/scale*Math.sin(arg);
					u[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
					u[2]=rad*Math.cos(arg);
					rad=Math.sqrt(v[0]*v[0]+v[1]*v[1]);
					arg=scale*arctan(v[1],v[0]);
					v[0]=2*rad/scale*Math.sin(arg);
					v[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
					v[2]=rad*Math.cos(arg);
					if (u[2]>0 && v[2]>0) for (int j=-1;j<2;j++)
						g.drawLine(SZ+(int)(u[0])+fudge,(int)(u[1])+j,SZ+(int)(v[0])+fudge,(int)(v[1])+j);
					else g.fillOval(SZ+(int)(u[0])+fudge,(int)(u[1]), 2, 2);
					if (getLabelShown() && i==30*m && u[2]>0)
						g.drawString(displayText,SZ+(int)(u[0])+fudge,(int)(u[1]));
					}
					break;
				}
		}
    }
  }  

  public boolean mouseIsOver(double[] v0, int SZ){
	    double[] v2={0,0,0},v1={0,0,0},r={0,0,0};
	    constList.get(1).getXYZ(v2);
		if (type==PERP || type==BISECTOR || type==PARLL0) {
		  v1[0]=v2[0]+(x/2);
		  v1[1]=v2[1]+(y/2);
		  v1[2]=0;
		  v2[0]-=(x/2);
		  v2[1]-=(y/2);
		}
		else {//(type==LINE || type==SEGMENT || type==RAY)
		  constList.get(0).getXYZ(v1);
		  resetSecondVector(v1,v2);
		}
		double dist=1e8;
		int k=1100; if (constList.get(0).getType()==RAY ||
				constList.get(0).getType()==SEGMENT) k=100;
		int j=-1000; if(constList.get(0).getType()==SEGMENT) j=0;
		for (int i=j;i<=k;i++) {
		  r[0]=v1[0]*i/100+v2[0]*(100-i)/100;
		  r[1]=v1[1]*i/100+v2[1]*(100-i)/100;
		  double arg=arctan(r[1],r[0]);
		  double rad=Math.sqrt(r[0]*r[0]+r[1]*r[1]);
		  for (int m=0;m<scale;m++) {
			  r[0]=rad*Math.sin(arg+m*2*Math.PI/scale);
			  r[1]=rad*Math.cos(arg+m*2*Math.PI/scale);
			  if (MathEqns.norm(r,v0)<dist) dist=MathEqns.norm(r,v0);
		  }
		}
	    return (dist<5);
	  }
  public void update() {
    boolean nowValid=true;
	for (int i=0;i<getSize();i++) nowValid=(nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (nowValid) {
        constList.get(0).getNewXYZ(v1);
        constList.get(1).getNewXYZ(v2);
		resetSecondVector(v1,v2);
		if (type==LINE || type==SEGMENT || type==RAY) {
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

class ConicalCircle extends ConicalConstruct{

  public ConicalCircle(int t, LinkedList<GeoConstruct> clickedList) {
	super(t,clickedList);
	if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
  }
  public ConicalPoint intersect(int m, ConicalConstruct a){
    ConicalPoint newPoint;
    if(a.getType()==0)
      newPoint=intersect(m,(ConicalCircle)a);
    else
      newPoint=intersect(m,(ConicalLine)a);
    return newPoint;
  }
  public ConicalPoint intersect(int m, ConicalLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	ConicalPoint newPoint=new ConicalPoint(CIRCintLINE0+m,tempList,v1);
    return newPoint;	// intersections not implemented in Conical
  }

  public ConicalPoint intersect(int m, ConicalCircle a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	ConicalPoint newPoint=new ConicalPoint(CIRCintCIRC00+m,tempList,v1);
    return newPoint;	// intersections not implemented in Conical
  }

  public void draw(Graphics g, int SZ, boolean New) {
    if ((New && isRealNew) || (!New && isReal)) {
      double[] center={0,0,0},radial={0,0,0},u={0,0,0},v={0,0,0};
	  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
      if(New){
        constList.get(0).getNewXYZ(center);
        constList.get(1).getNewXYZ(radial);
      }
      else{
        constList.get(0).getXYZ(center);
        constList.get(1).getXYZ(radial);
      }
      resetSecondVector(center,radial);
	  double rad=MathEqns.norm(center,radial),arg,rad1;
	  double[] w={0,0,0},z={0,0,0};
	  for (int i=-355;i<355;i++){// 355/113. is slightly larger than pi
		u[0]=center[0]+rad*Math.sin(i/113.);
		u[1]=center[1]+rad*Math.cos(i/113.);
		v[0]=center[0]+rad*Math.sin((i+1)/113.);
		v[1]=center[1]+rad*Math.cos((i+1)/113.);
		w[0]=u[0];	w[1]=u[1];
		z[0]=v[0];	z[1]=v[1];
		resetSecondVector(center,w);
		resetSecondVector(center,z);
		if (MathEqns.norm(center,radial)<MathEqns.norm(center,w)+.01 &&
			MathEqns.norm(center,radial)<MathEqns.norm(center,z)+.01)
		  switch (GeoPlayground.model){
		  case 0:
			  	rad1=Math.sqrt(u[0]*u[0]+u[1]*u[1]);
				arg=scale*arctan(u[1],u[0]);
				u[0]=rad1*Math.sin(arg);
				u[1]=rad1*Math.cos(arg);
				rad1=Math.sqrt(v[0]*v[0]+v[1]*v[1]);
				arg=scale*arctan(v[1],v[0]);
				v[0]=rad1*Math.sin(arg);
				v[1]=rad1*Math.cos(arg);
				g.drawLine(SZ+(int)(u[0])+fudge,SZ+(int)(u[1]),SZ+(int)(v[0])+fudge,SZ+(int)(v[1]));
				if (getLabelShown() && i==0)
					g.drawString(displayText,
							SZ+(int)(w[0])+fudge,SZ+(int)(w[1]));
			  break;
		  case 1:
			  for (int j=0;j<scale;j++) {
					w[0]=Math.cos(2*Math.PI*j/scale)*u[0]+Math.sin(2*Math.PI*j/scale)*u[1];
					w[1]=-Math.sin(2*Math.PI*j/scale)*u[0]+Math.cos(2*Math.PI*j/scale)*u[1];
					z[0]=Math.cos(2*Math.PI*j/scale)*v[0]+Math.sin(2*Math.PI*j/scale)*v[1];
					z[1]=-Math.sin(2*Math.PI*j/scale)*v[0]+Math.cos(2*Math.PI*j/scale)*v[1];
					g.drawLine(SZ+(int)(w[0])+fudge,SZ+(int)(w[1]),SZ+(int)(z[0])+fudge,SZ+(int)(z[1]));
					if (getLabelShown() && i==0)
						g.drawString(displayText,
								SZ+(int)(w[0])+fudge,SZ+(int)(w[1]));
			  }
			  break;
		  case 2:
		  case 3:
				rad1=Math.sqrt(u[0]*u[0]+u[1]*u[1]);
				arg=scale*arctan(u[1],u[0]);
				u[0]=2*rad1/scale*Math.sin(arg);
				u[1]=2*rad1*Math.sqrt(1.-1./(scale*scale));
				u[2]=rad1*Math.cos(arg);
				rad1=Math.sqrt(v[0]*v[0]+v[1]*v[1]);
				arg=scale*arctan(v[1],v[0]);
				v[0]=2*rad1/scale*Math.sin(arg);
				v[1]=2*rad1*Math.sqrt(1.-1./(scale*scale));
				v[2]=rad1*Math.cos(arg);
				if (u[2]>0 && v[2]>0) for (int j=-1;j<2;j++)
					g.drawLine(SZ+(int)(u[0])+fudge,(int)(u[1])+j,SZ+(int)(v[0])+fudge,(int)(v[1])+j);
				else g.fillOval(SZ+(int)(u[0])+fudge,(int)(u[1]), 2, 2);
				if (getLabelShown() && i==30 && u[2]>0)
					g.drawString(displayText,SZ+(int)(u[0])+fudge,(int)(u[1]));
			  break;
		  }
		}
	  }
  }
  public boolean mouseIsOver(double[] mouse, int SZ){
	  double[] center={0,0,0},radial={0,0,0},u={0,0,0},w={0,0,0};
	  double dist=1e8;
      constList.get(0).getNewXYZ(center);
      constList.get(1).getNewXYZ(radial);
      resetSecondVector(center,radial);
	  double rad=MathEqns.norm(center,radial);
	  for (int i=-355;i<355;i++){// 355/113. is slightly larger than pi
		u[0]=center[0]+rad*Math.sin(i/113.);
		u[1]=center[1]+rad*Math.cos(i/113.);
		w[0]=u[0];	w[1]=u[1];
		resetSecondVector(center,w);
		if (MathEqns.norm(center,radial)<MathEqns.norm(center,w)+.01)
			if (MathEqns.norm(u,mouse)<dist) {
				dist=MathEqns.norm(u,mouse);
			}
	  }
	return (dist<5);
  }

  public void update() {
    setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
  }

  public double[] getCLxyz(ConicalPoint inter, ConicalConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
    constList.get(0).getXYZ(u);          // a is a circle w/ ctr A 
    constList.get(1).getXYZ(v);          //& pt B
    b.getXYZ(w);          // b is a line with slope w
    b.constList.get(1).getXYZ(z);        // and point z, we find a pt of intersection.
        
    inter.setValid(CircleEqns.calculateEucCL(u,v,w,z,x,(i==0)));
    return x;
  }


  public double[] getNewCLxyz(ConicalPoint inter,ConicalConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
    constList.get(0).getNewXYZ(u);          // a is a circle w/ ctr A & pt B
    constList.get(1).getNewXYZ(v);          // b is a line
    b.getNewXYZ(w);          // we find a pt of intersection.
    b.constList.get(1).getNewXYZ(z);
    
    inter.setValid(CircleEqns.calculateEucCL(u,v,w,z,x,(i==0)));
    return x;
  }

  public double[] getCCxyz(ConicalPoint inter,ConicalConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getXYZ(t);          // a is a circle
    constList.get(1).getXYZ(u);          // b is a circle
    b.get(0).getXYZ(v);       // we find a pt of intersection.
    b.get(1).getXYZ(w);
    inter.setValid(CircleEqns.calculateEucCC(t,u,v,w,x,(i==0)));
    return x;
  }
  public double[] getNewCCxyz(ConicalPoint inter,ConicalConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getNewXYZ(t);         // a is a circle
    constList.get(1).getNewXYZ(u);         // b is a circle
    b.get(0).getNewXYZ(v);      // we find a pt of intersection.
    b.get(1).getNewXYZ(w);
    inter.isRealNew=CircleEqns.calculateEucCC(t,u,v,w,x,(i==0));
    return x;
  }  
}

class ConicalPTonLINE extends ConicalPoint {
	public ConicalPTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class ConicalPTonCIRC extends ConicalPoint {
	public ConicalPTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}
class ConicalMIDPT extends ConicalPoint {
	public ConicalMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
		setNewXYZ(nm);
	}
}

class ConicalFIXedPT extends ConicalPoint {
	public ConicalFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}
class ConicalTranslatePt extends ConicalPoint {
	public ConicalTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
}
class ConicalMeasure extends ConicalPoint {
    protected double[] a={0,0,0},b={0,0,0};
    protected double rad,arg,dist;
	public ConicalMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
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
		resetSecondVector(a,b);
		dist=MathEqns.norm(a,b)/30.;
		rad=MathEqns.norm(v1);
		arg=arctan(v1[1],v1[0]);
		switch(GeoPlayground.model) {
	    case 0:
	    	v1[0]=SZ+rad*Math.sin(scale*arg);
	    	v1[1]=SZ+rad*Math.cos(scale*arg);
		    g.fillRect((int)v1[0]-sz/2+1+fudge,(int)v1[1]-sz/2+1,sz-2,sz-2);
			g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
		  break;
		case 1:
			sz=(int)MathEqns.max(sz-2,2);
			for (int i=0;i<scale;i++) {
				v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
				v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.fillRect((int)(v1[0]-sz/2+fudge),(int)(v1[1])-sz/2,sz,sz);
				g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
			}
			break;
		case 2:
		case 3:
			v1[0]=SZ+2*rad/scale*Math.sin(scale*arg);
			v1[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
			v1[2]=2*rad/scale*Math.cos(scale*arg);
			if (v1[2]>0) {
			sz=(int)MathEqns.max(sz-2,2);
			g.fillRect((int)v1[0]-sz/2+fudge,(int)v1[1]-sz/2,sz,sz);
			g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
			}
			break;
	  }
	 }
	}
}
class ConicalCOMMENT extends ConicalPoint {
	public ConicalCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		double rad=MathEqns.norm(v1);
		double arg=arctan(v1[1],v1[0]);
		switch(GeoPlayground.model) {
	    case 0:
	    	v1[0]=SZ+rad*Math.sin(scale*arg);
	    	v1[1]=SZ+rad*Math.cos(scale*arg);
		    g.fillRect((int)v1[0]-sz/2+1+fudge,(int)v1[1]-sz/2+1,sz-2,sz-2);
			g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
			g.drawString(displayText,(int)v1[0]+13+fudge,(int)v1[1]);
		  break;
		case 1:
			sz=(int)MathEqns.max(sz-2,2);
			for (int i=0;i<scale;i++) {
				v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
				v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.fillRect((int)(v1[0]-sz/2+fudge),(int)(v1[1])-sz/2,sz,sz);
				g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
				g.drawString(displayText,(int)v1[0]+13+fudge,(int)v1[1]);
			}
			break;
		case 2:
		case 3:
			v1[0]=SZ+2*rad/scale*Math.sin(scale*arg);
			v1[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
			v1[2]=2*rad/scale*Math.cos(scale*arg);
			if (v1[2]>0) {
			sz=(int)MathEqns.max(sz-2,2);
			g.fillRect((int)v1[0]-sz/2+fudge,(int)v1[1]-sz/2,sz,sz);
			g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
			g.drawString(displayText,(int)v1[0]+13+fudge,(int)v1[1]);
			}
			break;
	  }
	}
}class ConicalCONSTANT extends ConicalPoint {
	public ConicalCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		String displayMeasure=""+MathEqns.chop(measureValue,GeoPlayground.digits);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if ((New && getValidNew()) || (!New && getValid())) {
			double rad=MathEqns.norm(v1);
			double arg=arctan(v1[1],v1[0]);
			switch(GeoPlayground.model) {
		    case 0:
		    	v1[0]=SZ+rad*Math.sin(scale*arg);
		    	v1[1]=SZ+rad*Math.cos(scale*arg);
			    g.fillRect((int)v1[0]-sz/2+1+fudge,(int)v1[1]-sz/2+1,sz-2,sz-2);
				g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
				g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				
			  break;
			case 1:
				sz=(int)MathEqns.max(sz-2,2);
				for (int i=0;i<scale;i++) {
					v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
					v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
					g.fillRect((int)(v1[0]-sz/2+fudge),(int)(v1[1])-sz/2,sz,sz);
					g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
					g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				}
				break;
			case 2:
			case 3:
				v1[0]=SZ+2*rad/scale*Math.sin(scale*arg);
				v1[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
				v1[2]=2*rad/scale*Math.cos(scale*arg);
				if (v1[2]>0) {
				sz=(int)MathEqns.max(sz-2,2);
				g.fillRect((int)v1[0]-sz/2+fudge,(int)v1[1]-sz/2,sz,sz);
				g.drawLine((int)v1[0]+fudge,(int)v1[1],(int)v1[0]+10+fudge,(int)v1[1]-5);
				g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				}
				break;
			}
		 }
	}
}

class ConicalSUM extends ConicalMeasure {
	public ConicalSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			String displayMeasure;
			if (!New) {
				if (getType()==SUM)		measureValue=get(0).measureValue+get(1).measureValue;
				if (getType()==DIFF)	measureValue=get(0).measureValue-get(1).measureValue;
				if (getType()==PROD)	measureValue=get(0).measureValue*get(1).measureValue;
				displayMeasure=""+MathEqns.chop(measureValue,GeoPlayground.digits);
			}
			else {
				if (getType()==SUM)		measureValueNew=get(0).measureValueNew+get(1).measureValueNew;
				if (getType()==DIFF)	measureValueNew=get(0).measureValueNew-get(1).measureValueNew;
				if (getType()==PROD)	measureValueNew=get(0).measureValueNew*get(1).measureValueNew;
				displayMeasure=""+MathEqns.chop(measureValueNew,GeoPlayground.digits);
			}
			switch (GeoPlayground.model) {
			case 0:
				g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				break;
			case 1:
				for (int i=0;i<scale;i++) {
					v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
					v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
					g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				}
				break;
			case 2:
			case 3:
				if (v1[2]>0)
					g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				break;
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
class ConicalRATIO extends ConicalMeasure {
	public ConicalRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			String displayMeasure="";
			if (!New) {
				if (get(1).measureValue!=0) {
					measureValue=get(0).measureValue/get(1).measureValue;
					displayMeasure=""+MathEqns.chop(measureValue,GeoPlayground.digits);
				}
			}
			else {
				if (get(1).measureValueNew!=0) {
					measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
					displayMeasure=""+MathEqns.chop(measureValueNew,GeoPlayground.digits);
				}
			}
			switch (GeoPlayground.model) {
			case 0:
				g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				break;
			case 1:
				for (int i=0;i<scale;i++) {
					v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
					v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
					g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				}
				break;
			case 2:
			case 3:
				if (v1[2]>0)
					g.drawString(displayText+"\u2248"+displayMeasure,(int)v1[0]+13+fudge,(int)v1[1]);
				break;
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
class ConicalDISTANCE extends ConicalMeasure {
	public ConicalDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1; update();
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		dist=measureValue;
		if (New) dist=measureValueNew;
		switch (GeoPlayground.model) {
			case 0:
				g.drawString(displayText+"\u2248"
					+MathEqns.chop(dist,GeoPlayground.digits),(int)v1[0]+13+fudge,(int)v1[1]);
				break;
			case 1:
				for (int i=0;i<scale;i++) {
					v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
					v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
					g.drawString(displayText+"\u2248"+MathEqns.chop(dist,GeoPlayground.digits),(int)v1[0]+13+fudge,(int)v1[1]);
				}
				break;
			case 2:
			case 3:
				if (v1[2]>0)
					g.drawString(displayText+"\u2248"
							+MathEqns.chop(dist,GeoPlayground.digits),(int)v1[0]+13+fudge,(int)v1[1]);
				break;
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			resetSecondVector(a,b);
			measureValueNew=MathEqns.norm(a,b)/30.;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			resetSecondVector(a,b);
			measureValue=MathEqns.norm(a,b)/30.;
		}
	}
}
class ConicalTRIANGLE extends ConicalMeasure {
	public ConicalTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		String displayString;
		update();
		if (New) {
			displayString=""+MathEqns.chop(measureValueNew,GeoPlayground.digits);
		}
		else {
			displayString=""+MathEqns.chop(measureValue,GeoPlayground.digits);
		}
		switch (GeoPlayground.model) {
		case 0:
			g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		case 1:
			for (int i=0;i<scale;i++) {
				v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
				v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			}
			break;
		case 2:
		case 3:
			if (v1[2]>0)
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		}
	}
	public void update() {
		super.update();
		double[] c={0,0,0};
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			double[] d={c[0],c[1],c[2]};
			resetSecondVector(a,b);	resetSecondVector(a,c);	resetSecondVector(b,d);
			if (MathEqns.norm(c,d)>.01) { // Cone Point in Triangle
				double  s=MathEqns.norm(a)/30.,
						t=MathEqns.norm(b)/30.,
						u=MathEqns.norm(a,b)/30.,
						p=(s+t+u)/2;
				measureValueNew=Math.sqrt(p*(p-s)*(p-t)*(p-u));
				t=MathEqns.norm(c)/30.;
				u=MathEqns.norm(a,c)/30.;
				p=(s+t+u)/2;
				measureValueNew+=Math.sqrt(p*(p-s)*(p-t)*(p-u));
				s=MathEqns.norm(b)/30.;
				t=MathEqns.norm(d)/30.;
				u=MathEqns.norm(b,d)/30.;
				p=(s+t+u)/2;
				measureValueNew+=Math.sqrt(p*(p-s)*(p-t)*(p-u));
			}
			else {// Cone Point exterior to Triangle
				double	s=MathEqns.norm(a,b)/30.,
						t=MathEqns.norm(a,c)/30.,
						u=MathEqns.norm(b,c)/30.,
						p=(s+t+u)/2;
				measureValueNew=Math.sqrt(p*(p-s)*(p-t)*(p-u));
			}
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			for (int i=0;i<3;i++) d[i]=c[i];
			resetSecondVector(a,b);	resetSecondVector(a,c);	resetSecondVector(b,d);
			if (MathEqns.norm(c,d)>.01) { // Cone Point in Triangle
				double  s=MathEqns.norm(a)/30.,
						t=MathEqns.norm(b)/30.,
						u=MathEqns.norm(a,b)/30.,
						p=(s+t+u)/2;
				measureValue=Math.sqrt(p*(p-s)*(p-t)*(p-u));
				t=MathEqns.norm(c)/30.;
				u=MathEqns.norm(a,c)/30.;
				p=(s+t+u)/2;
				measureValue+=Math.sqrt(p*(p-s)*(p-t)*(p-u));
				s=MathEqns.norm(b)/30.;
				t=MathEqns.norm(d)/30.;
				u=MathEqns.norm(b,d)/30.;
				p=(s+t+u)/2;
				measureValue+=Math.sqrt(p*(p-s)*(p-t)*(p-u));
			}
			else {// Cone Point exterior to Triangle
				double	s=MathEqns.norm(a,b)/30.,
						t=MathEqns.norm(a,c)/30.,
						u=MathEqns.norm(b,c)/30.,
						p=(s+t+u)/2;
				measureValue=Math.sqrt(p*(p-s)*(p-t)*(p-u));
			}
			
		}
	}
}
class ConicalANGLE extends ConicalMeasure {
	public ConicalANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		double[] c={0,0,0};
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
		}
		resetSecondVector(b,a);
		resetSecondVector(b,c);
		String displayString=""+MathEqns.chop(MathEqns.eucAngle(a,b,c),GeoPlayground.digits);
		if (!GeoPlayground.degrees) displayString=""+MathEqns.chop(MathEqns.eucAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits);
		switch (GeoPlayground.model) {
		case 0:
			g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		case 1:
			for (int i=0;i<scale;i++) {
				v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
				v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			}
			break;
		case 2:
		case 3:
			if (v1[2]>0)
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
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

class ConicalCIRCUMF extends ConicalMeasure {
	public ConicalCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		String displayString;
		update();
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	displayString=""+MathEqns.chop(measureValueNew,GeoPlayground.digits);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);		displayString=""+MathEqns.chop(measureValue,GeoPlayground.digits);
		}
		resetSecondVector(b,a);
		switch (GeoPlayground.model) {
		case 0:
			g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		case 1:
			for (int i=0;i<scale;i++) {
				v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
				v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			}
			break;
		case 2:
		case 3:
			if (v1[2]>0)
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		}
	}
	public void update() {
		super.update();
		boolean selfIntersects=false;
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			double[] u={0,0,0};
		    resetSecondVector(a,b);
			double rad=MathEqns.norm(a,b);
			measureValueNew=0;
			for (int i=-355;i<355;i++){// 355/113. is slightly larger than pi
				u[0]=a[0]+rad*Math.sin(i/113.);
				u[1]=a[1]+rad*Math.cos(i/113.);
				resetSecondVector(a,u);
				if (MathEqns.norm(a,b)>MathEqns.norm(a,u)+.0001)	selfIntersects=true;
				else {
					double[] v={a[0]+rad*Math.sin((i+1)/113.),a[1]+rad*Math.cos((i+1)/113.),0};
					measureValueNew+=MathEqns.norm(u,v);
				}
			}
			if (selfIntersects) measureValueNew=measureValueNew/30.;
			else measureValueNew=2*Math.PI*MathEqns.norm(a,b)/30.;
			if (MathEqns.norm(a)==0) measureValueNew/=scale;
			get(0).getXYZ(a);		get(1).getXYZ(b);	selfIntersects=false;
			resetSecondVector(a,b);
			rad=MathEqns.norm(a,b);
			measureValue=0;
			for (int i=-355;i<355;i++){// 355/113. is slightly larger than pi
				u[0]=a[0]+rad*Math.sin(i/113.);
				u[1]=a[1]+rad*Math.cos(i/113.);
				resetSecondVector(a,u);
				if (MathEqns.norm(a,b)>MathEqns.norm(a,u)+.0001)	selfIntersects=true;
				else {
					double[] v={a[0]+rad*Math.sin((i+1)/113.),a[1]+rad*Math.cos((i+1)/113.),0};
					measureValue+=MathEqns.norm(u,v);
				}
			}
			if (selfIntersects) measureValue=measureValue/30.;
			else measureValue=2*Math.PI*MathEqns.norm(a,b)/30.;		
			if (MathEqns.norm(a)==0) measureValue/=scale;
		}
	}
}

class ConicalAREA extends ConicalMeasure {
	public ConicalAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		String displayString;
		update();
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	displayString=""+MathEqns.chop(measureValueNew,GeoPlayground.digits);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);		displayString=""+MathEqns.chop(measureValue,GeoPlayground.digits);
		}
		resetSecondVector(b,a);
		switch (GeoPlayground.model) {
		case 0:
			g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		case 1:
			for (int i=0;i<scale;i++) {
				v1[0]=SZ+rad*Math.sin(arg+2*i*Math.PI/scale);
				v1[1]=SZ+rad*Math.cos(arg+2*i*Math.PI/scale);
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			}
			break;
		case 2:
		case 3:
			if (v1[2]>0)
				g.drawString(displayText+"\u2248"+displayString,(int)(v1[0])+13+fudge,(int)(v1[1]));
			break;
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] center={0,0,0}, origin={0,0,0}, radial={0,0,0}, junk={0,0,0};
			get(0).getNewXYZ(center);	get(1).getNewXYZ(radial);
			measureValueNew=0;
			boolean originInsideCircle=(MathEqns.norm(center)<MathEqns.norm(center,radial));
			LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
			EuclideanPoint o = new EuclideanPoint(POINT,temp,origin);
			EuclideanPoint c = new EuclideanPoint(POINT,temp,center);
			EuclideanPoint r = new EuclideanPoint(POINT,temp,radial);
			junk[0]=Math.sin(arctan(center[1],center[0])+Math.PI/scale);
			junk[1]=Math.cos(arctan(center[1],center[0])+Math.PI/scale);
			EuclideanPoint j = new EuclideanPoint(POINT,temp,junk);
			temp.add(o);		temp.add(j);
			EuclideanLine line = new EuclideanLine(LINE,temp);
			line.update();		line.getNewXYZ(junk);	line.setXYZ(junk);
			temp.clear();		temp.add(c);			temp.add(r);
			EuclideanCircle circ = new EuclideanCircle(CIRCLE,temp);
			EuclideanPoint i0,i1;
			i0=((EuclideanLine)line).intersect(0,(EuclideanCircle)circ);
			i0.update();	i0.getNewXYZ(junk);	i0.setXYZ(junk);
			i1=((EuclideanLine)line).intersect(1,(EuclideanCircle)circ);
			i1.update();	i1.getNewXYZ(junk);	i1.setXYZ(junk);
			// here we created the circle and the line with slope pi/scale radians away from circle's center
			// if that line intersects the circle, then we need to estimate the area.  we do that by adding
			// the areas of triangles with vertices (origin & two points on far side of circle) and, if
			// needed, subtracting the areas of triangles with vertices (origin & 2 pts on near side of circle).
			if (i0.getValidNew() && i1.getValidNew()) {// 2 intersections
				for (int i=0;i<100.*Math.PI/scale-1;i++) {
					junk[0]=Math.sin(arctan(center[1],center[0])+i/100.);
					junk[1]=Math.cos(arctan(center[1],center[0])+i/100.);
					temp.clear();
					j = new EuclideanPoint(POINT,temp,junk);
					temp.add(o);		temp.add(j);
					line = new EuclideanLine(LINE,temp);
					i0=((EuclideanLine)line).intersect(0,(EuclideanCircle)circ);
					i0.update();	i0.getNewXYZ(junk);	i0.setXYZ(junk);
					i1=((EuclideanLine)line).intersect(1,(EuclideanCircle)circ);
					i1.update();	i1.getNewXYZ(v2);	i1.setXYZ(v2);
					if (MathEqns.norm(v2)<MathEqns.norm(junk)) {// make sure i1 > i0
						i0.setXYZ(v2);	i1.setXYZ(junk);
					}
					i0.getXYZ(v2);		i1.getXYZ(junk);
					double 	s0=MathEqns.norm(v2),
							s1=MathEqns.norm(junk);
					junk[0]=Math.sin(arctan(center[1],center[0])+(i+1)/100.);
					junk[1]=Math.cos(arctan(center[1],center[0])+(i+1)/100.);
					temp.clear();
					j = new EuclideanPoint(POINT,temp,junk);
					temp.add(o);		temp.add(j);
					line = new EuclideanLine(LINE,temp);
					EuclideanPoint j0,j1;
					j0=((EuclideanLine)line).intersect(0,(EuclideanCircle)circ);
					j0.update();	j0.getNewXYZ(junk);	j0.setXYZ(junk);
					j1=((EuclideanLine)line).intersect(1,(EuclideanCircle)circ);
					j1.update();	j1.getNewXYZ(v2);	j1.setXYZ(v2);
					if (MathEqns.norm(v2)<MathEqns.norm(junk)) {// make sure j1 > j0
						j0.setXYZ(v2);	j1.setXYZ(junk);
					}
					j0.getXYZ(v2);		j1.getXYZ(junk);
					double 	t0=MathEqns.norm(v2),
							t1=MathEqns.norm(junk);
					i1.getXYZ(v2);
					double	u1=MathEqns.norm(v2,junk);
					i0.getXYZ(v2);		j0.getXYZ(junk);
					double 	u0=MathEqns.norm(v2,junk);
					double	p0=(s0+t0+u0)/2,
							p1=(s1+t1+u1)/2;
					measureValueNew+=2*Math.sqrt(p1*(p1-s1)*(p1-t1)*(p1-u1));
					if (!originInsideCircle) measureValueNew-=2*Math.sqrt(p0*(p0-s0)*(p0-t0)*(p0-u0));
				}
			}
			else {// 0 intersections
				measureValueNew=Math.PI*Math.pow(MathEqns.norm(center,radial),2);
			}
			measureValueNew/=900.;
			get(0).getXYZ(center);		get(1).getXYZ(radial);
			measureValue=0;
			originInsideCircle=(MathEqns.norm(center)<MathEqns.norm(center,radial));
			temp.clear();
			c = new EuclideanPoint(POINT,temp,center);
			r = new EuclideanPoint(POINT,temp,radial);
			junk[0]=Math.sin(arctan(center[1],center[0])+Math.PI/scale);
			junk[1]=Math.cos(arctan(center[1],center[0])+Math.PI/scale);
			j = new EuclideanPoint(POINT,temp,junk);
			temp.add(o);		temp.add(j);
			line = new EuclideanLine(LINE,temp);
			line.update();		line.getNewXYZ(junk);	line.setXYZ(junk);
			temp.clear();		temp.add(c);			temp.add(r);
			circ = new EuclideanCircle(CIRCLE,temp);
			i0=((EuclideanLine)line).intersect(0,(EuclideanCircle)circ);
			i0.update();	i0.getNewXYZ(junk);	i0.setXYZ(junk);
			i1=((EuclideanLine)line).intersect(1,(EuclideanCircle)circ);
			i1.update();	i1.getNewXYZ(junk);	i1.setXYZ(junk);
			if (i0.getValidNew() && i1.getValidNew()) {// 2 intersections
				for (int i=0;i<100.*Math.PI/scale-1;i++) {
					junk[0]=Math.sin(arctan(center[1],center[0])+i/100.);
					junk[1]=Math.cos(arctan(center[1],center[0])+i/100.);
					temp.clear();
					j = new EuclideanPoint(POINT,temp,junk);
					temp.add(o);		temp.add(j);
					line = new EuclideanLine(LINE,temp);
					i0=((EuclideanLine)line).intersect(0,(EuclideanCircle)circ);
					i0.update();	i0.getNewXYZ(junk);	i0.setXYZ(junk);
					i1=((EuclideanLine)line).intersect(1,(EuclideanCircle)circ);
					i1.update();	i1.getNewXYZ(v2);	i1.setXYZ(v2);
					if (MathEqns.norm(v2)<MathEqns.norm(junk)) {// make sure i1 > i0
						i0.setXYZ(v2);	i1.setXYZ(junk);
					}
					i0.getXYZ(v2);		i1.getXYZ(junk);
					double 	s0=MathEqns.norm(v2),
							s1=MathEqns.norm(junk);
					junk[0]=Math.sin(arctan(center[1],center[0])+(i+1)/100.);
					junk[1]=Math.cos(arctan(center[1],center[0])+(i+1)/100.);
					temp.clear();
					j = new EuclideanPoint(POINT,temp,junk);
					temp.add(o);		temp.add(j);
					line = new EuclideanLine(LINE,temp);
					EuclideanPoint j0,j1;
					j0=((EuclideanLine)line).intersect(0,(EuclideanCircle)circ);
					j0.update();	j0.getNewXYZ(junk);	j0.setXYZ(junk);
					j1=((EuclideanLine)line).intersect(1,(EuclideanCircle)circ);
					j1.update();	j1.getNewXYZ(v2);	j1.setXYZ(v2);
					if (MathEqns.norm(v2)<MathEqns.norm(junk)) {// make sure j1 > j0
						j0.setXYZ(v2);	j1.setXYZ(junk);
					}
					j0.getXYZ(v2);		j1.getXYZ(junk);
					double 	t0=MathEqns.norm(v2),
							t1=MathEqns.norm(junk);
					i1.getXYZ(v2);
					double	u1=MathEqns.norm(v2,junk);
					i0.getXYZ(v2);		j0.getXYZ(junk);
					double 	u0=MathEqns.norm(v2,junk);
					double	p0=(s0+t0+u0)/2,
							p1=(s1+t1+u1)/2;
					measureValue+=2*Math.sqrt(p1*(p1-s1)*(p1-t1)*(p1-u1));
					if (!originInsideCircle) measureValue-=2*Math.sqrt(p0*(p0-s0)*(p0-t0)*(p0-u0));
				}
			}
			else {// 0 intersections
				measureValue=Math.PI*Math.pow(MathEqns.norm(center,radial),2);
			}
			measureValue/=900.;
		}
	}
}

class ConicalPERP extends ConicalLine {
	public ConicalPERP(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
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
class ConicalPARLL0 extends ConicalLine {
	public ConicalPARLL0(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
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

class ConicalSEGMENT extends ConicalLine {
	public ConicalSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		double rad,arg;
		double[] u={0,0,0},v={0,0,0},a={vec2[0]-vec1[0],vec2[1]-vec1[1],0};
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	  	switch(GeoPlayground.model){
		case 0:
			if (MathEqns.norm(vec1,vec2)>.00001) for (int i=0;i<64;i++) {
				u[0]=vec1[0]+i/64.*a[0];
				u[1]=vec1[1]+i/64.*a[1];
				v[0]=vec1[0]+(i+1)/64.*a[0];
				v[1]=vec1[1]+(i+1)/64.*a[1];
			rad=Math.sqrt(u[0]*u[0]+u[1]*u[1]);
			arg=scale*arctan(u[1],u[0]);
			u[0]=rad*Math.sin(arg);
			u[1]=rad*Math.cos(arg);
			rad=Math.sqrt(v[0]*v[0]+v[1]*v[1]);
			arg=scale*arctan(v[1],v[0]);
			v[0]=rad*Math.sin(arg);
			v[1]=rad*Math.cos(arg);
			g.drawLine(SZ+(int)(u[0])+fudge,SZ+(int)(u[1]),SZ+(int)(v[0])+fudge,SZ+(int)(v[1]));
			if (getLabelShown() && i==100)
				g.drawString(displayText,SZ+(int)(u[0])+fudge,SZ+(int)(u[1]));
			}
			break;
		case 1:
			double[] w={0,0,0},z={0,0,0};
			if (MathEqns.norm(vec1,vec2)>.00001) {
				u[0]=vec1[0];		v[0]=vec1[0]+a[0];
				u[1]=vec1[1];		v[1]=vec1[1]+a[1];
				for (int j=0;j<scale;j++) {
				w[0]=Math.cos(2*Math.PI*j/scale)*u[0]+Math.sin(2*Math.PI*j/scale)*u[1];
				w[1]=-Math.sin(2*Math.PI*j/scale)*u[0]+Math.cos(2*Math.PI*j/scale)*u[1];
				z[0]=Math.cos(2*Math.PI*j/scale)*v[0]+Math.sin(2*Math.PI*j/scale)*v[1];
				z[1]=-Math.sin(2*Math.PI*j/scale)*v[0]+Math.cos(2*Math.PI*j/scale)*v[1];
				g.drawLine(SZ+(int)(w[0])+fudge,SZ+(int)(w[1]),SZ+(int)(z[0])+fudge,SZ+(int)(z[1]));
				if (getLabelShown())
					g.drawString(displayText,SZ+(int)(.7*w[0]+.3*z[0])+fudge,SZ+(int)(.7*w[1]+.3*z[1]));
				}
			}
			break;
		case 2:
		case 3:
			if (MathEqns.norm(vec1,vec2)>.00001) for (int i=0;i<64;i++) {
				u[0]=vec1[0]+i/64.*a[0];
				u[1]=vec1[1]+i/64.*a[1];
				v[0]=vec1[0]+(i+1)/64.*a[0];
				v[1]=vec1[1]+(i+1)/64.*a[1];
			rad=Math.sqrt(u[0]*u[0]+u[1]*u[1]);
			arg=scale*arctan(u[1],u[0]);
			u[0]=2*rad/scale*Math.sin(arg);
			u[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
			u[2]=rad*Math.cos(arg);
			rad=Math.sqrt(v[0]*v[0]+v[1]*v[1]);
			arg=scale*arctan(v[1],v[0]);
			v[0]=2*rad/scale*Math.sin(arg);
			v[1]=2*rad*Math.sqrt(1.-1./(scale*scale));
			v[2]=rad*Math.cos(arg);
			if (u[2]>0 && v[2]>0) for (int j=-1;j<2;j++)
				g.drawLine(SZ+(int)(u[0])+fudge,(int)(u[1])+j,SZ+(int)(v[0])+fudge,(int)(v[1])+j);
			else g.fillOval(SZ+(int)(u[0])+fudge,(int)(u[1]), 2, 2);
			if (getLabelShown() && i==100 && u[2]>0)
				g.drawString(displayText,SZ+(int)(u[0])+fudge,(int)(u[1]));
			}
			break;
		}
	}
	public void update() {
		super.update();
	}
}

class ConicalBISECTOR extends ConicalLine {

	public ConicalBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
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
