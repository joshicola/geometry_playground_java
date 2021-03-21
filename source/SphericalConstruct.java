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
 
import java.awt.*;
import java.util.LinkedList;

abstract class SphericalConstruct extends GeoConstruct {
	protected static double scale=0;//Powers of two
	double[] v1={0,0,0},v2={0,0,0};
	
  public SphericalConstruct(int t, LinkedList<GeoConstruct> clickedList){
	  type=t;
	  for(int i=0;i<clickedList.size();i++)
		  constList.addLast(clickedList.get(i));
	  if (constList.size()==2) {
	    double[] vec1={0,0,0}, vec2={0,0,0};
	    constList.get(0).getXYZ(vec1);
        constList.get(1).getXYZ(vec2);
        this.setXYZ(vec1, vec2);
	  }
  }
  public SphericalConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v){
	  type=t;
	  for(int i=0;i<clickedList.size();i++)
		  constList.addLast(clickedList.get(i));
	  if (t==ANGLE) {
		    double[] v0={0,0,0};
			constList.get(0).getXYZ(v);
			constList.get(2).getXYZ(v0);
			for (int i=0;i<2;i++) v[i]=(v[i]+v0[i])/2;
	  }
	  if (t==DISTANCE || t==RATIO || t==SUM || t==DIFF || t==PROD) {
		double[] v0={0,0,0};
		constList.get(0).getXYZ(v);
		constList.get(1).getXYZ(v0);
		for (int i=0;i<2;i++) v[i]=(v[i]+v0[i])/2;
	  }
	  if (t==TRIANGLE) {
		    double[] v0={0,0,0},v1={0,0,0};
			constList.get(0).getXYZ(v);
			constList.get(1).getXYZ(v1);
			constList.get(2).getXYZ(v0);
			for (int i=0;i<2;i++) v[i]=(v[i]+v0[i]+v1[i])/3;
	  }
	  if (t==CONSTANT) {
		  measureValue=v[2];
		  measureValueNew=v[2];
		  v[2]=Math.sqrt(1-v[0]*v[0]-v[1]*v[1]);
	  }
	  setXYZ(v);
	  update();
  }
   
  public abstract SphericalPoint intersect(int m,SphericalConstruct a);
  
  public void setXYZ(double[] vector) {
	MathEqns.normalize(vector);
	x=vector[0]; y=vector[1]; z=vector[2];
	newX=x;		newY=y;		newZ=z;
    }
  public void setXYZ(double[] v1, double[] v2) {
    v2[2]+=.00000001;
    double[] normal={v1[1]*v2[2]-v2[1]*v1[2],
	                 v2[0]*v1[2]-v1[0]*v2[2],
					 v1[0]*v2[1]-v2[0]*v1[1]}; // normal vector	   
	setXYZ(normal);
    }
  public void setNewXYZ(double[] vector) {
    MathEqns.normalize(vector);
	newX=vector[0]; newY=vector[1]; newZ=vector[2];
  }
  public void setNewXYZ(double[] v1, double[] v2) {
    v2[2]+=.00000001;
	double[] normal={v1[1]*v2[2]-v2[1]*v1[2],
	                 v2[0]*v1[2]-v1[0]*v2[2],
					 v1[0]*v2[1]-v2[0]*v1[1]}; // normal vector	   
	setNewXYZ(normal);
  }

  public void translate(double[] dragStart, double[] dragNow){
    double[] norm={0,0,0};
    double w=0;

    MathEqns.crossProduct(dragStart,dragNow,norm);    
    double theta=Math.acos(MathEqns.dotProduct(dragNow,dragStart));
    w=Math.cos(theta/2);
    for (int i=0;i<3;i++) norm[i]*=Math.sin(theta/2);

    MathEqns.rotate(w,norm[0],norm[1],norm[2],this);
  }
  public static double getScale(){
		return Math.exp(scale*Math.log(2));
	}
	public static void setScale(double s){
		if (GeoPlayground.model==0) resetScale();
		else if ((s<0 && scale>-2) || (s>0 && scale<5))	scale+=s;
	}
	public static void resetScale(){scale=0;}
	public static void rescale(double[] v){
		v[0]=v[0]*getScale();
		v[1]=v[1]*getScale();
	}
	public static void unscale(double[] v){
		v[0]=v[0]/getScale();
		v[1]=v[1]/getScale();
	}
	public void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow) {
    MathEqns.transform(fixedObject,this,dragStart,dragNow);
  }
} // end class 


class SphericalPoint extends SphericalConstruct{
	protected int sz;
  public SphericalPoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}
  
  public SphericalPoint intersect(int m, SphericalConstruct a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	SphericalPoint newPoint=new SphericalPoint(LINEintLINE0+m,tempList,v1);
  	return newPoint;
  }
    
  public void draw(Graphics g, int SZ, boolean New) {
   sz=(int)MathEqns.max(4,SZ/40);
   if ((New && isRealNew) || (!New && isReal)) { 
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	if (New) getNewXYZ(v1);
    else getXYZ(v1);
    switch (GeoPlayground.model) {
	  case 0:
	    break;
	  case 1:
	    if (v1[2]==-1) v1[2]=-.99999999;
	    v1[0]/=(1+v1[2]);
		v1[1]/=(1+v1[2]);
		rescale(v1);
		break;
	  case 2:
	    if (v1[1]==1) v1[1]=.99999999;
		if (v1[1]==-1) v1[1]=-.99999999;
		double phi=Math.acos(v1[1]), theta=Math.acos(v1[0]/Math.sqrt(1-v1[1]*v1[1]));
		if (v1[2]<0) theta*=-1;
		v1[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
		v1[0]=-theta/Math.PI;
	    break;//*/
	}
	if ((v1[2]>0 && GeoPlayground.model==0) || GeoPlayground.model!=0) {
	  if (getType()<30) {
		g.fillOval(SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2,sz,sz);
		if (getLabelShown())
		  g.drawString(displayText,SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2);
	  }
	 }
   }
  }
  
  public boolean mouseIsOver(double[] v1, int SZ){
    double sz=MathEqns.max(6,SZ/20);
    double[] v={v1[0],v1[1],v1[2]};
    unscale(v);
    double m=Math.sqrt(1+v[0]*v[0]+v[1]*v[1]);
    return ((x-v1[0])*(x-v1[0])+(y-v1[1])*(y-v1[1])+(z-v1[2])*(z-v1[2])<sz*sz/m/m/(SZ*SZ)/getScale()/getScale());
  }
  
  public void updatePTonLINE() {
	  double[] nm={0,0,0},bn={0,0,0};
	  setValidNew(get(0).getValidNew());
	  if (getValidNew()) {
		  constList.get(0).getNewXYZ(v2);
		  MathEqns.crossProduct(v1,v2,nm);
		  MathEqns.crossProduct(v2,nm,bn);
		  MathEqns.normalize(bn);
		  if (constList.get(0).getType()==SEGMENT) {
				double[] a={0,0,0},b={0,0,0};
				constList.get(0).get(0).getNewXYZ(a);
				constList.get(0).get(1).getNewXYZ(b);
				double 	av=Math.acos(MathEqns.dotProduct(a,bn)),
						vb=Math.acos(MathEqns.dotProduct(bn,b)),
						ab=Math.acos(MathEqns.dotProduct(a,b));
				if (Math.abs(av+vb-ab)>.0001) {
					if (av>vb) constList.get(0).get(1).getNewXYZ(bn);
					else constList.get(0).get(0).getNewXYZ(bn);
				}
		  }
		  setNewXYZ(bn);
	  }
  }
  public void updatePTonCIRC() {
	  double[] nm={0,0,0},bn={0,0,0};
	  setValidNew(get(0).getValidNew());
	  if (getValidNew()) {
		  constList.get(0).get(0).getNewXYZ(v2);
		  MathEqns.crossProduct(v1,v2,nm);
		  MathEqns.crossProduct(v2,nm,bn);
		  MathEqns.normalize(bn);
		  constList.get(0).get(1).getNewXYZ(nm);
		  double phi=Math.acos(MathEqns.dotProduct(v2,nm));
		  for (int i=0;i<3;i++) v1[i]=Math.cos(phi)*v2[i]+Math.sin(phi)*bn[i];
		  setNewXYZ(v1);
	  }
  }
  public void update() {
	boolean nowValid=true;
	for (int i=0;i<constList.size();i++) nowValid = (nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (type!=RATIO && type!=SUM && type!=DIFF && type!=PROD) getNewXYZ(v1);
  }
}

class SphericalLine extends SphericalConstruct{
  double[] vec1={0,0,0}, vec2={0,0,0};
  double[] norm={0,0,0},binorm={0,0,0};
  
  public SphericalLine(int t, LinkedList<GeoConstruct> clickedList){super(t,clickedList);}
  
  public SphericalPoint intersect(int m, SphericalConstruct a){
  	SphericalPoint newPoint;
  	if(a.getType()==0)
  		newPoint=intersect(m,(SphericalCircle)a);
  	else
  		newPoint=intersect(m,(SphericalLine)a);
  	return newPoint;
  }

  public SphericalPoint intersect(int m, SphericalLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	SphericalPoint newPoint=new SphericalLINEintLINE(LINEintLINE0+m,tempList,v1);
	if (m>0) {
	  newPoint.x*=-1;		newPoint.y*=-1;		newPoint.z*=-1;
	  newPoint.newX*=-1;	newPoint.newY*=-1;	newPoint.newZ*=-1;
	}
  	return newPoint;
  }
  
  public SphericalPoint intersect(int m, SphericalCircle a){
  	SphericalPoint newPoint=a.intersect(m,this);
  	return newPoint;
  }

  public void draw(Graphics g, int SZ, boolean New) {
   if ((New && isRealNew) || (!New && isReal)) {
	if(New){
      constList.get(0).getNewXYZ(vec1);	constList.get(1).getNewXYZ(vec2);
    }
    else{
      constList.get(0).getXYZ(vec1);	constList.get(1).getXYZ(vec2);
    }
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	MathEqns.crossProduct(vec1, vec2, norm);
    MathEqns.crossProduct(vec1, norm, binorm);
	if (type==BISECTOR) {
	  if (New) {
	    getNewXYZ(vec2);	constList.get(1).getNewXYZ(vec1);
	  }
	  else {
	    getXYZ(vec2);		constList.get(1).getXYZ(vec1);
	  }
	  MathEqns.crossProduct(vec1, vec2, binorm);
	}
    double[] v1={0,0,0},v2={0,0,0};
	if (type!=SEGMENT)
    for (int i=-44*GeoPlayground.model;i<44;i++) {
      v1[0]=vec1[0]*Math.cos(i/14.)+binorm[0]*Math.sin(i/14.);
      v1[1]=vec1[1]*Math.cos(i/14.)+binorm[1]*Math.sin(i/14.);
      v1[2]=vec1[2]*Math.cos(i/14.)+binorm[2]*Math.sin(i/14.);
      v2[0]=vec1[0]*Math.cos((i+1)/14.)+binorm[0]*Math.sin((i+1)/14.);
      v2[1]=vec1[1]*Math.cos((i+1)/14.)+binorm[1]*Math.sin((i+1)/14.);
      v2[2]=vec1[2]*Math.cos((i+1)/14.)+binorm[2]*Math.sin((i+1)/14.);
      switch (GeoPlayground.model) {
		  case 0:
		    if (v1[2]*v2[2]>0) {
			  MathEqns.normalize(v1);
			  MathEqns.normalize(v2);
			  MathEqns.makeStandard(v1);
			  MathEqns.makeStandard(v2);
			  g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
                         SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
			}
			else if (getLabelShown()) 
			  g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
			break;
		  case 1:
		    if (v1[2]==-1) v1[2]=-.99999999;
			if (v2[2]==-1) v2[2]=-.99999999;
			v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
			v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
			rescale(v1);rescale(v2);
			if (v1[2]>-.95 && v2[2]>-.95)
			g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
                       SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
			if (v1[2]*v2[2]<=0 && getLabelShown() && i<=0)
			  g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
			break;
		  case 2:
		    if (v1[1]==1) v1[1]=.99999999;
		    if (v2[1]==1) v2[1]=.99999999;
		    if (v1[1]==-1) v1[1]=-.99999999;
			if (v2[1]==-1) v2[1]=-.99999999;
			double phi=Math.acos(v1[1]),
		       theta=Math.acos(v1[0]/Math.sqrt(1-v1[1]*v1[1]));
			if (v1[2]<0) theta*=-1;
			v1[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
			v1[0]=-theta/Math.PI;
			phi=Math.acos(v2[1]);
			theta=Math.acos(v2[0]/Math.sqrt(1-v2[1]*v2[1]));
			if (v2[2]<0) theta*=-1;
			v2[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
			v2[0]=-theta/Math.PI;
			if ((v1[0]-v2[0])*(v1[0]-v2[0])+(v1[1]-v2[1])*(v1[1]-v2[1])<1.5)
			  g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
                         SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
			if (getLabelShown() && i==11)
			  g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
			break;
	  }
	}
   }
  }  

  public boolean mouseIsOver(double[] v1, int SZ){
    double[] v2={0,0,0};
	v2[0]=x;v2[1]=y;v2[2]=z;
    return Math.abs(Math.asin(MathEqns.dotProduct(v1,v2)))< 0.02/getScale();
  }
  public void update() {
    double[] v1={0,0,0},v2={0,0,0};
	boolean nowValid=true;
	for (int i=0;i<getSize();i++) nowValid=(nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (nowValid) {
	    constList.get(0).getNewXYZ(v1);
	    constList.get(1).getNewXYZ(v2);
		if (type!=BISECTOR) {
			if (MathEqns.norm(v1,v2)<.00001) setValidNew(false);
			else setNewXYZ(v1,v2);
		}
	}
  }
}

class SphericalCircle extends SphericalConstruct{
    double[] vec1={0,0,0}, vec2={0,0,0};
	double[] norm={0,0,0},binorm={0,0,0};
  public SphericalCircle(int t, LinkedList<GeoConstruct> clickedList) {
	super(t,clickedList);
	if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
  }
  public SphericalPoint intersect(int m, SphericalConstruct a){
  	SphericalPoint newPoint;
  	if(a.getType()==0)
  		newPoint=intersect(m,(SphericalCircle)a);
  	else
  		newPoint=intersect(m,(SphericalLine)a);
  	return newPoint;
  }
  public SphericalPoint intersect(int m, SphericalLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	SphericalPoint newPoint=new SphericalCIRCintLINE(CIRCintLINE0+m,tempList,v1);
  	newPoint.setXYZ(getCLxyz(newPoint,a,m));
  	return newPoint;
  }
  
  public SphericalPoint intersect(int m, SphericalCircle a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	SphericalPoint newPoint=new SphericalCIRCintCIRC(CIRCintCIRC00+m,tempList,v1);
  	newPoint.setXYZ(getCCxyz(newPoint,a,m));
  	return newPoint;
  }
  
  public void draw(Graphics g, int SZ, boolean New) {
   if ((New && isRealNew) || (!New && isReal)) {
    double[] axis={0,0,0}, p0={0,0,0}, p1={0,0,0}, p2={0,0,0}, p3={0,0,0};
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	if(New){
      constList.get(0).getNewXYZ(axis);
      constList.get(1).getNewXYZ(p0);
    }
    else{
      constList.get(0).getXYZ(axis);
      constList.get(1).getXYZ(p0);
    }
	double d=MathEqns.dotProduct(axis,p0);
	for (int i=0;i<3;i++) p1[i]=p0[i]-d*axis[i];
	MathEqns.crossProduct(axis,p1,p2);
	MathEqns.normalize(p2);
	LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
	SphericalPoint x0 = new SphericalPoint(0,temp,axis);
	SphericalPoint x1 = new SphericalPoint(0,temp,p2);
	temp.add(x0); temp.add(x1);
	SphericalLine x2 = new SphericalLine(LINE,temp);
	x2.update(); x2.getNewXYZ(p3); x2.setXYZ(p3);
	SphericalPoint x3;
	x3=((SphericalLine)x2).intersect(0,(SphericalCircle)this);
	x3.update(); x3.getNewXYZ(p3);
	d=Math.sqrt(MathEqns.dotProduct(p1,p1));
	for (int i=0;i<3;i++) p2[i]*=d;
	d=Math.sqrt(1-MathEqns.dotProduct(p2,p2));
	if (Math.acos(MathEqns.dotProduct(axis,p0))>Math.PI/2) d*=-1;
	for (int i=0;i<88;i++) {
	  double[] v1={0,0,0},v2={0,0,0};
	  for (int j=0;j<3;j++) {
	    v1[j]=p1[j]*Math.cos(i/14.)+p2[j]*Math.sin(i/14.)+d*axis[j];
		v2[j]=p1[j]*Math.cos((i+1)/14.)+p2[j]*Math.sin((i+1)/14.)+d*axis[j];
	  }
      switch (GeoPlayground.model) {
	    case 0:
		  if (v1[2]>0 && v2[2]>0) {
			g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
					   SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
		  }
		  break;
		case 1:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  if (v2[2]==-1) v2[2]=-.99999999;
		  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
		  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
		  rescale(v1);rescale(v2);
		  g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
					 SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
		  break;
		case 2:
		  if (v1[1]==1) v1[1]=.99999999;
		  if (v2[1]==1) v2[1]=.99999999;
		  if (v1[1]==-1) v1[1]=-.99999999;
		  if (v2[1]==-1) v2[1]=-.99999999;
		  double phi=Math.acos(v1[1]),
				 psi=Math.acos(v1[0]/Math.sqrt(1-v1[1]*v1[1]));
		  if (v1[2]<0) psi*=-1;
		  v1[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
		  v1[0]=-psi/Math.PI;
		  phi=Math.acos(v2[1]);
		  psi=Math.acos(v2[0]/Math.sqrt(1-v2[1]*v2[1]));
		  if (v2[2]<0) psi*=-1;
		  v2[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
		  v2[0]=-psi/Math.PI;
		  if ((v1[0]-v2[0])*(v1[0]-v2[0])+(v1[1]-v2[1])*(v1[1]-v2[1])<1.5)
			g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
                       SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
		  break;
	  }
    }
	for (int j=0;j<3;j++) p2[j]=p2[j]+d*axis[j];
	if (getLabelShown()) switch(GeoPlayground.model) {
	  case 0:
	    if (p2[2]>0)
		  g.drawString(displayText,SZ+(int)(SZ*p2[0])+fudge,SZ+(int)(SZ*p2[1]));
	    break;
	  case 1:
	    if (p3[2]==-1) p3[2]=-.99999999;
		p3[0]/=(1+p3[2]);	p3[1]/=(1+p3[2]);
		rescale(p3);
	    if (p3[2]>0)
		  g.drawString(displayText,SZ+(int)(SZ*p3[0])+fudge,SZ+(int)(SZ*p3[1]));
		else
		  g.drawString(displayText,SZ-(int)(SZ*p3[0])+fudge,SZ-(int)(SZ*p3[1]));
	    break;
	  case 2:
	    if (p2[1]==1) p2[1]=.99999999;
		if (p2[1]==-1) p2[1]=-.99999999;
		double phi=Math.acos(p2[1]), theta=Math.acos(p2[0]/Math.sqrt(1-p2[1]*p2[1]));
		if (p2[2]<0) theta*=-1;
		p2[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
		p2[0]=-theta/Math.PI;
		g.drawString(displayText,SZ+(int)(SZ*p2[0])+fudge,SZ+(int)(SZ*p2[1]));
	    break;
	
	}
   }
  }
  public boolean mouseIsOver(double[] mouse, int SZ){
    double[] axis={0,0,0}, point={0,0,0};
	constList.get(0).getXYZ(axis);
	constList.get(1).getXYZ(point);
	double theta=Math.acos(MathEqns.dotProduct(axis,point));
	for (int i=0;i<3;i++) point[i]=-axis[i];
	if (Math.asin(axis[2])<theta)
	  if (Math.abs(Math.acos(MathEqns.dotProduct(axis,mouse))-theta)< 0.02/getScale() ||
	      Math.abs(Math.acos(MathEqns.dotProduct(point,mouse))-theta)< 0.02/getScale()) return true;
	  else return false;
    else return Math.abs(Math.acos(MathEqns.dotProduct(axis,mouse))-theta)< 0.02/getScale();
  }
  
   public void update() {
     setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
  }
  
  public double[] getCLxyz(SphericalPoint inter, SphericalConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getXYZ(u);					// a is a circle
	constList.get(1).getXYZ(v);					// b is a line
	b.getXYZ(w);					// we find a pt of intersection.
	if (i==0) inter.setValid(CircleEqns.calculateCL(u,v,w,x,true));
	else inter.setValid(CircleEqns.calculateCL(u,v,w,x,false));
	return x;
  }
  
  public double[] getNewCLxyz(SphericalPoint inter,SphericalConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getNewXYZ(u);					// a is a circle
	constList.get(1).getNewXYZ(v);					// b is a line
	b.getNewXYZ(w);					// we find a pt of intersection.
	if (i==0) inter.isRealNew=CircleEqns.calculateCL(u,v,w,x,true);
	else inter.isRealNew=CircleEqns.calculateCL(u,v,w,x,false);
	return x;
  }
  
  public double[] getCCxyz(SphericalPoint inter,SphericalConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getXYZ(t);					// a is a circle
	constList.get(1).getXYZ(u);					// b is a circle
	b.get(0).getXYZ(v);				// we find a pt of intersection.
	b.get(1).getXYZ(w);
	switch (i) {
	  case 0: inter.setValid(CircleEqns.calculateCC0(t,u,v,w,x,true));  break;
	  case 1: inter.setValid(CircleEqns.calculateCC0(t,u,v,w,x,false)); break;
	}
	return x;
  }
  public double[] getNewCCxyz(SphericalPoint inter,SphericalConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getNewXYZ(t);					// a is a circle
	constList.get(1).getNewXYZ(u);					// b is a circle
	b.get(0).getNewXYZ(v);			// we find a pt of intersection.
	b.get(1).getNewXYZ(w);
	switch (i) {
	  case 0: inter.isRealNew=CircleEqns.calculateCC0(t,u,v,w,x,true);  break;
	  case 1: inter.isRealNew=CircleEqns.calculateCC0(t,u,v,w,x,false); break;
	}
	return x;
  }  
}

class SphericalPTonLINE extends SphericalPoint {
	public SphericalPTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class SphericalPTonCIRC extends SphericalPoint {
	public SphericalPTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}

class SphericalLINEintLINE extends SphericalPoint {
	public SphericalLINEintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		double[] nm={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		MathEqns.crossProduct(v1,v2,nm);
		MathEqns.normalize(nm);
		if (type==LINEintLINE1) for (int i=0;i<3;i++) nm[i]*=-1;
		setValidNew(true);
		setNewXYZ(nm);
		for (int i=0;i<2;i++) if (constList.get(i).getType()==SEGMENT) {
			constList.get(i).get(0).getNewXYZ(v1);
			constList.get(i).get(1).getNewXYZ(v2);
			if (Math.abs(Math.acos(MathEqns.dotProduct(v1,v2))-Math.acos(MathEqns.dotProduct(v1,nm))-Math.acos(MathEqns.dotProduct(nm,v2)))>.0001)
				setValidNew(false);
		}
	}
}


class SphericalCIRCintLINE extends SphericalPoint {
	public SphericalCIRCintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		setNewXYZ(((SphericalCircle)constList.get(0)).getNewCLxyz(this,(SphericalConstruct)constList.get(1),type-CIRCintLINE0));
		double[] nm={newX,newY,newZ};
		for (int i=0;i<2;i++) if (constList.get(i).getType()==SEGMENT) {
			constList.get(i).get(0).getNewXYZ(v1);
			constList.get(i).get(1).getNewXYZ(v2);
			if (Math.abs(Math.acos(MathEqns.dotProduct(v1,v2))-Math.acos(MathEqns.dotProduct(v1,nm))-Math.acos(MathEqns.dotProduct(nm,v2)))>.0001)
				setValidNew(false);
		}
	}
}

class SphericalCIRCintCIRC extends SphericalPoint {
	public SphericalCIRCintCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		setNewXYZ(((SphericalCircle)constList.get(0)).getNewCCxyz(this,(SphericalConstruct)constList.get(1),type-CIRCintCIRC00));
	}
}

class SphericalMIDPT extends SphericalPoint {
	public SphericalMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		for (int i=0;i<3;i++) v1[i]+=v2[i];
		MathEqns.normalize(v1);
		setNewXYZ(v1);
		  
	}
}

class SphericalFIXedPT extends SphericalPoint {
	public SphericalFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}
class SphericalReflectPt extends SphericalPoint {
	public SphericalReflectPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).getNewXYZ(u);
		constList.get(1).getNewXYZ(v);
		tempList.add(constList.get(0).get(0));
		tempList.add(constList.get(0).get(1));
		SphericalLine l;
		if (constList.get(0).getType()==BISECTOR) {
			tempList.add(constList.get(0).get(2));
			l = new SphericalBISECTOR(BISECTOR,tempList);
		}
		else if (constList.get(0).getType()==PERP) l = new SphericalPERP(PERP,tempList);
		else l = new SphericalLine(constList.get(0).getType(),tempList);
		l.update();		l.getNewXYZ(u);		l.setXYZ(u);	tempList.clear();
		SphericalPoint p = new SphericalPoint(POINT,tempList,v);
		// If the point is on the line of reflection,
		// the reflected point is just the point, hence
		if (Math.abs(MathEqns.dotProduct(u,v))<.000000001) setNewXYZ(v);
		else { // otherwise
			tempList.clear();
			tempList.add(l);
			tempList.add(p);
			SphericalPERP a=new SphericalPERP(PERP,tempList);
			a.update();		a.getNewXYZ(u);		a.setXYZ(u);
			SphericalPoint b,x0,x1;
			b=((SphericalLine)a).intersect(0,(SphericalLine)l);
			b.update();	b.getNewXYZ(u);	b.setXYZ(u);
			tempList.clear();	tempList.add(b);	tempList.add(p);
			SphericalCircle c=new SphericalCircle(CIRCLE,tempList);
			x0=((SphericalLine)a).intersect(0,(SphericalCircle)c);
			x0.update();	x0.getNewXYZ(v);	x0.setXYZ(v);
			x1=((SphericalLine)a).intersect(1,(SphericalCircle)c);
			x1.update();	x1.getNewXYZ(w);	x1.setXYZ(w);
			p.getNewXYZ(u);
			if (MathEqns.norm(u,v)>MathEqns.norm(u,w)) setNewXYZ(v);
			else setNewXYZ(w);
		}	
	}
}
class SphericalTranslatePt extends SphericalPoint {
	public SphericalTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		SphericalPoint a,b,c;
		constList.get(0).get(0).getNewXYZ(w);	a=new SphericalPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new SphericalPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			c=new SphericalPoint(POINT,tempList,w);
		tempList.add(a);	tempList.add(b);
		SphericalLine d = new SphericalLine(LINE,tempList);
		d.update();			d.getNewXYZ(w);		d.setXYZ(w);
		SphericalMIDPT e = new SphericalMIDPT(MIDPT,tempList,w);
		e.update();			e.getNewXYZ(w);		e.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(a);
		SphericalPERP f = new SphericalPERP(PERP,tempList);
		f.update();			f.getNewXYZ(w);		f.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(e);
		SphericalPERP g = new SphericalPERP(PERP,tempList);
		g.update();			g.getNewXYZ(w);		g.setXYZ(w);
		tempList.clear();	tempList.add(f);	tempList.add(c);
		SphericalReflectPt h = new SphericalReflectPt(REFLECT_PT,tempList,w);
		h.update();			h.getNewXYZ(w);		h.setXYZ(w);
		tempList.clear();	tempList.add(g);	tempList.add(h);
		SphericalReflectPt i = new SphericalReflectPt(REFLECT_PT,tempList,w);
		i.update();			i.getNewXYZ(w);		setNewXYZ(w);
	}
}
class SphericalInvertPt extends SphericalPoint {
	public SphericalInvertPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).get(0).getNewXYZ(u);
		constList.get(0).get(1).getNewXYZ(v);
		constList.get(1).getNewXYZ(w);
		if (MathEqns.norm(u,w)<.00001 || MathEqns.norm(u,v)>1.9999) {
			for (int i=0;i<3;i++) w[i]*=-1;
			setNewXYZ(w);
		}
		else {
			double[] c0={0,0,0},c1={0,0,0},pt={0,0,0};
			u[0]*=-1;u[1]*=-1;
		    MathEqns.sphTranslate(u,v,c1);
			MathEqns.sphTranslate(u,w,pt);
			if (c1[2]==-1) c1[2]=-.99999999;
			if (pt[2]==-1) pt[2]=-.99999999;
			c1[0]/=(1+c1[2]);	c1[1]/=(1+c1[2]);	c1[2]=0;
			pt[0]/=(1+pt[2]);	pt[1]/=(1+pt[2]);	pt[2]=0;
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			EuclideanPoint ec0=new EuclideanPoint(POINT,tempList,c0);
			EuclideanPoint ec1=new EuclideanPoint(POINT,tempList,c1);
			EuclideanPoint ept=new EuclideanPoint(POINT,tempList,pt);
			tempList.add(ec0);	tempList.add(ec1);
			EuclideanCircle crc=new EuclideanCircle(CIRCLE,tempList);
			tempList.clear();	tempList.add(crc);	tempList.add(ept);
			EuclideanPoint inv=new EuclideanInvertPt(INVERT_PT,tempList,pt);
		    inv.update();	inv.getNewXYZ(pt);
		    double d=1/(1+pt[0]*pt[0]+pt[1]*pt[1]);
		    pt[2]=(1-pt[0]*pt[0]-pt[1]*pt[1])*d;
		    pt[1]*=(2*d);	pt[0]*=(2*d);
		    constList.get(0).get(0).getNewXYZ(u);
		    MathEqns.sphTranslate(u,pt,c0);
		    setNewXYZ(c0);
		}
	}
}
class SphericalRotatePt extends SphericalPoint {
	public SphericalRotatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		SphericalPoint a,b,c,d;
		constList.get(0).get(0).getNewXYZ(w);	a=new SphericalPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new SphericalPoint(POINT,tempList,w);
		constList.get(0).get(2).getNewXYZ(w);	c=new SphericalPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			d=new SphericalPoint(POINT,tempList,w);
		tempList.add(b);	tempList.add(a);
		SphericalLine l0 = new SphericalLine(LINE,tempList);
		l0.update();		l0.getNewXYZ(w);	l0.setXYZ(w);
		tempList.clear();	tempList.add(a);	tempList.add(b);	tempList.add(c);
		SphericalBISECTOR l1 = new SphericalBISECTOR(BISECTOR,tempList);
		l1.update();		l1.getNewXYZ(w);	l1.setXYZ(w);
		tempList.clear();	tempList.add(l0);	tempList.add(d);
		SphericalReflectPt p0 = new SphericalReflectPt(REFLECT_PT,tempList,w);
		p0.update();		p0.getNewXYZ(w);	p0.setXYZ(w);
		tempList.clear();	tempList.add(l1);	tempList.add(p0);
		SphericalReflectPt p1 = new SphericalReflectPt(REFLECT_PT,tempList,w);
		p1.update();		p1.getNewXYZ(w);	setNewXYZ(w);
	}
}
class SphericalMeasure extends SphericalPoint {
    protected double[] a={0,0,0},b={0,0,0};
	public SphericalMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
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
		if (v1[2]>0 || GeoPlayground.model>0) {
			g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
			g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		}
	  }
	}
}
class SphericalCOMMENT extends SphericalPoint {
	public SphericalCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText,SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
}
class SphericalCONSTANT extends SphericalPoint {
	public SphericalCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (v1[2]>0 || GeoPlayground.model>0) {
			g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
			g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
			g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
}
class SphericalSUM extends SphericalMeasure {
	public SphericalSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if (!New) {
				if (getType()==SUM)		measureValue=get(0).measureValue+get(1).measureValue;
				if (getType()==DIFF)	measureValue=get(0).measureValue-get(1).measureValue;
				if (getType()==PROD)	measureValue=get(0).measureValue*get(1).measureValue;
				if (v1[2]>0 || GeoPlayground.model>0) {
					g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
				}
			}
			else {
				if (getType()==SUM)		measureValueNew=get(0).measureValueNew+get(1).measureValueNew;
				if (getType()==DIFF)	measureValueNew=get(0).measureValueNew-get(1).measureValueNew;
				if (getType()==PROD)	measureValueNew=get(0).measureValueNew*get(1).measureValueNew;
				if (v1[2]>0 || GeoPlayground.model>0) {
					g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
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
class SphericalRATIO extends SphericalMeasure {
	public SphericalRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		update();
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (!New) {
			if (get(1).measureValue!=0) {
				measureValue=get(0).measureValue/get(1).measureValue;
				if (v1[2]>0 || GeoPlayground.model>0) {
					g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
				}
			}
		}
		else {
			if (get(1).measureValueNew!=0) {
				measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
				if (v1[2]>0 || GeoPlayground.model>0) {
					g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
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


class SphericalDISTANCE extends SphericalMeasure {
	public SphericalDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (v1[2]>0 || GeoPlayground.model>0)
		g.drawString(displayText+"\u2248"
				   +MathEqns.chop(Math.acos(MathEqns.dotProduct(a,b)),GeoPlayground.digits),
				   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=Math.acos(MathEqns.dotProduct(a,b));
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=Math.acos(MathEqns.dotProduct(a,b));
		}
	}
}

class SphericalTRIANGLE extends SphericalMeasure {
	public SphericalTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		double[] c={0,0,0},d={0,0,0};
		double aa,bb,cc;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		  if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		  }
		  else {
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		  }
		  for (int i=0;i<3;i++) d[i]=-1*b[i];
		  if (v1[2]>0 || GeoPlayground.model>0) {
			  g.drawString(displayText+"\u2248"+MathEqns.chop(aa+bb+cc-Math.PI,GeoPlayground.digits),
					SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		  }
		
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0};
			double aa,bb,cc;
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
		    get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		    bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
		    get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		    cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
			measureValueNew=aa+bb+cc-Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		    measureValue=aa+bb+cc-Math.PI;
		}
	}
}
class SphericalANGLE extends SphericalMeasure {
	public SphericalANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
		if (v1[2]>0 || GeoPlayground.model>0) {
			if (GeoPlayground.degrees)
					g.drawString(displayText+"\u2248"
						       +MathEqns.chop(MathEqns.sphAngle(a,b,c),GeoPlayground.digits)+"\u00b0",
						       SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
			else
				g.drawString(displayText+"\u2248"
					       +MathEqns.chop(MathEqns.sphAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits),
					       SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0};
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			measureValueNew=MathEqns.sphAngle(a,b,c);
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			measureValue=MathEqns.sphAngle(a,b,c);
			if (!GeoPlayground.degrees) {
				measureValueNew=measureValueNew/180.*Math.PI;
				measureValue=measureValue/180.*Math.PI;
			}
		}
	}
}

class SphericalCIRCUMF extends SphericalMeasure {
	public SphericalCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (v1[2]>0 || GeoPlayground.model>0)
		g.drawString(displayText+"\u2248"
				+MathEqns.chop(2*Math.PI*Math.sqrt(1-MathEqns.dotProduct(a,b)*MathEqns.dotProduct(a,b)),GeoPlayground.digits),
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=2*Math.sqrt(1-MathEqns.dotProduct(a,b)*MathEqns.dotProduct(a,b))*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=2*Math.sqrt(1-MathEqns.dotProduct(a,b)*MathEqns.dotProduct(a,b))*Math.PI;
		}
	}
}

class SphericalAREA extends SphericalMeasure {
	public SphericalAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (v1[2]>0 || GeoPlayground.model>0)
		g.drawString(displayText+"\u2248"
				+MathEqns.chop((2-2*MathEqns.dotProduct(a,b))*Math.PI,GeoPlayground.digits),
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=(2-2*MathEqns.dotProduct(a,b))*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=(2-2*MathEqns.dotProduct(a,b))*Math.PI;
		}
	}
}

class SphericalPERP extends SphericalLine {
	public SphericalPERP(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}

class SphericalSEGMENT extends SphericalLine {
	public SphericalSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		double m=512;
		if (GeoPlayground.model==1) m*=Math.sqrt(getScale());
		double theta=Math.acos(MathEqns.dotProduct(vec1,vec2));
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		  for (int i=0; i<theta*m;i++) {
			v1[0]=vec1[0]*Math.cos(i/m)-binorm[0]*Math.sin(i/m);
			v1[1]=vec1[1]*Math.cos(i/m)-binorm[1]*Math.sin(i/m);
			v1[2]=vec1[2]*Math.cos(i/m)-binorm[2]*Math.sin(i/m);
			v2[0]=vec1[0]*Math.cos((i+1)/m)-binorm[0]*Math.sin((i+1)/m);
			v2[1]=vec1[1]*Math.cos((i+1)/m)-binorm[1]*Math.sin((i+1)/m);
			v2[2]=vec1[2]*Math.cos((i+1)/m)-binorm[2]*Math.sin((i+1)/m);
			switch (GeoPlayground.model) {
			  case 0:
			    if (v1[2]>0 && v2[2]>0) {
			    g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
	                       SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
				if (i>theta*m/3 && i<=theta*m/3+1 && getLabelShown())
			      g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
				}
				break;
			  case 1:
			    if (v1[2]==-1) v1[2]=-.99999999;
				if (v2[2]==-1) v2[2]=-.99999999;
				v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
				v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
				rescale(v1);rescale(v2);
				g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
	                       SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
				if (i>theta*m/3 && i<=theta*m/3+1 && v1[2]>0 && getLabelShown())
			      g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
				break;
			  case 2:
			    if (v1[1]==1) v1[1]=.99999999;
			    if (v2[1]==1) v2[1]=.99999999;
			    if (v1[1]==-1) v1[1]=-.99999999;
				if (v2[1]==-1) v2[1]=-.99999999;
				double phi=Math.acos(v1[1]),
			           psi=Math.acos(v1[0]/Math.sqrt(1-v1[1]*v1[1]));
				if (v1[2]<0) psi*=-1;
				v1[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
				v1[0]=-psi/Math.PI;
				phi=Math.acos(v2[1]);
				psi=Math.acos(v2[0]/Math.sqrt(1-v2[1]*v2[1]));
				if (v2[2]<0) psi*=-1;
				v2[1]=Math.log(Math.tan(Math.PI/2-phi/2))/Math.PI;
				v2[0]=-psi/Math.PI;
				if ((v1[0]-v2[0])*(v1[0]-v2[0])+(v1[1]-v2[1])*(v1[1]-v2[1])<1.5)
				  g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
	                         SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
				if (i>theta*m/3 && i<=theta*m/3+1 && v1[2]>0 && getLabelShown())
			      g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
				break;
		      }
		  }
	}
	public void update() {
		super.update();
		
	}
}

class SphericalBISECTOR extends SphericalLine {

	public SphericalBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		double[] aa={0,0,0},bb={0,0,0},cc={0,0,0},mp={0,0,0};
		constList.get(0).getNewXYZ(aa);
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		SphericalPoint a=new SphericalPoint(POINT,tempList,aa);
		constList.get(1).getNewXYZ(bb);
		SphericalPoint b=new SphericalPoint(POINT,tempList,bb);
		constList.get(2).getNewXYZ(cc);
		SphericalPoint c=new SphericalPoint(POINT,tempList,cc);
		tempList.add(a);	tempList.add(c);
		SphericalPoint m=new SphericalMIDPT(MIDPT,tempList,mp);
		m.update();	m.getNewXYZ(mp);	m.setXYZ(mp);
		if (Math.abs(MathEqns.determinant(aa,bb,cc))<.000001) {
			if (MathEqns.sphAngle(aa,bb,cc)>175) {
				tempList.clear();	tempList.add(a);	tempList.add(c);
				SphericalCircle d=new SphericalCircle(CIRCLE,tempList);
				tempList.clear();	tempList.add(c);	tempList.add(a);
				SphericalCircle e=new SphericalCircle(CIRCLE,tempList);
				SphericalPoint f1,f2;
				f1=((SphericalCircle)d).intersect(0,(SphericalCircle)e);
				f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
				f2=((SphericalCircle)d).intersect(1,(SphericalCircle)e);
				f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
				tempList.clear();	tempList.add(f1);	tempList.add(f2);
			}
			else {
				tempList.clear();	tempList.add(a);	tempList.add(b);
			}
		}
		else {
			tempList.clear();	tempList.add(b);	tempList.add(c);
			SphericalLine d=new SphericalLine(LINE,tempList);
			d.update();	d.getNewXYZ(v1);
			tempList.clear();	tempList.add(b);	tempList.add(a);
			SphericalCircle e=new SphericalCircle(CIRCLE,tempList);
			SphericalPoint f1,f2;
			f1=((SphericalLine)d).intersect(0,(SphericalCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((SphericalLine)d).intersect(1,(SphericalCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(f1);
			SphericalPoint g1=new SphericalMIDPT(MIDPT,tempList,v1);
		  	g1.update();	g1.getNewXYZ(v1);	g1.setXYZ(v1);
		  	tempList.clear();	tempList.add(a);	tempList.add(f2);
		  	SphericalPoint g2=new SphericalMIDPT(MIDPT,tempList,v2);
		  	g2.update();	g2.getNewXYZ(v2);	g2.setXYZ(v2);
		  	tempList.clear();	tempList.add(a);	tempList.add(c);
		  	SphericalPoint g3=new SphericalMIDPT(MIDPT,tempList,bb);
		  	g3.update();	g3.getNewXYZ(bb);	g3.setXYZ(bb);
		  	if (Math.abs(MathEqns.dotProduct(v1,bb))<Math.abs(MathEqns.dotProduct(v2,bb)))
				g1=g2;
		  	tempList.clear();	tempList.add(b);	tempList.add(g1);
		}
		SphericalLine h=new SphericalLine(LINE,tempList);
		h.update();	h.getNewXYZ(v1);
		if (MathEqns.norm(aa,bb)*MathEqns.norm(cc,bb)>.000000001) setNewXYZ(v1);
		else setValidNew(false);
	}
}
