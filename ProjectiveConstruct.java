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

abstract class ProjectiveConstruct extends GeoConstruct{
	protected static double scale=0;//Powers of two
	public static final int scaleLimit=3;
	protected double[] v1={0,0,0},v2={0,0,0};

  public ProjectiveConstruct(int t, LinkedList<GeoConstruct> clickedList){
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
  public ProjectiveConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v){
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
  
  public abstract ProjectivePoint intersect(int m,ProjectiveConstruct a);
  
  public void setXYZ(double[] vector) {
    MathEqns.normalize(vector);MathEqns.makeStandard(vector);
	x=vector[0]; y=vector[1]; z=vector[2]; 
	newX=x;		newY=y;		newZ=z;
  }
  public void setXYZ(double[] v1, double[] v2) {
    double[] normal={v1[1]*v2[2]-v2[1]*v1[2],
	                 v2[0]*v1[2]-v1[0]*v2[2],
					 v1[0]*v2[1]-v2[0]*v1[1]}; // normal vector	   
	if (MathEqns.norm(normal)>.00001) {setXYZ(normal);setValid(true);}
	else setValid(false);
  }
  public void setNewXYZ(double[] vector) {
    MathEqns.normalize(vector);MathEqns.makeStandard(vector);
	newX=vector[0]; newY=vector[1]; newZ=vector[2];
  }
  public void setNewXYZ(double[] v1, double[] v2) {
    double[] normal={v1[1]*v2[2]-v2[1]*v1[2],
	                 v2[0]*v1[2]-v1[0]*v2[2],
					 v1[0]*v2[1]-v2[0]*v1[1]}; // normal vector	   
    if (MathEqns.norm(normal)>.00001) {setNewXYZ(normal);setValidNew(true);}
	else setValidNew(false);
  }
  
  public void translate(double[] dragStart, double[] dragNow){
    double[] norm={0,0,0};
    MathEqns.crossProduct(dragStart,dragNow,norm);    
    double theta=Math.acos(MathEqns.dotProduct(dragNow,dragStart));
    double w=Math.cos(theta/2);
    for (int i=0;i<3;i++) norm[i]*=Math.sin(theta/2);
    MathEqns.rotate(w,norm[0],norm[1],norm[2],this);
  }
  public static double getScale(){
		return Math.exp(scale*Math.log(2));
	}
  public static void setScale(double s){
		if (GeoPlayground.model==0) resetScale();
		else if ((s<0 && scale>-3) || (s>0 && scale<5))	scale+=s;
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


class ProjectivePoint extends ProjectiveConstruct{
	protected int sz;
  public ProjectivePoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}

  
  public ProjectivePoint intersect(int m, GeoConstruct a){return null;}
  
  public ProjectivePoint intersect(int m, ProjectiveConstruct a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	ProjectivePoint newPoint=new ProjectivePoint(LINEintLINE0,tempList,v1);
  	return newPoint;
  }
    
  public void draw(Graphics g, int SZ, boolean New) {
   sz=(int)MathEqns.max(4,SZ/40);
   if ((New && isRealNew) || (!New && isReal)) { 
    if(New) getNewXYZ(v1);
    else	getXYZ(v1);
	switch (GeoPlayground.model) {
	  case 0:
	    break;
	  case 1:
	  case 2:
	  if (v1[2]==0) v1[2]=.00000001;
	    v1[0]/=v1[2];
		v1[1]/=v1[2];
		rescale(v1);
		break;
	}
	if (getType()<30) {
	  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	  g.fillOval(SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2,sz,sz);
	  if (getLabelShown())
	    g.drawString(displayText,SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2);
	}
   }
  }
  
  public boolean mouseIsOver(double[] v1, int SZ){
    double sz=MathEqns.max(6,SZ/20);
    double[] v2={x,y,z};
    double[] v={v1[0],v1[1],v1[2]};
    unscale(v);
    double m=1+Math.sqrt(v[0]*v[0]+v[1]*v[1]);
    return ((v2[0]-v1[0])*(v2[0]-v1[0])+(v2[1]-v1[1])*(v2[1]-v1[1])<sz*sz/m/m/(SZ*SZ)/getScale()/getScale());
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
				double 	av=MathEqns.min(Math.acos(MathEqns.dotProduct(a,bn)),Math.PI-Math.acos(MathEqns.dotProduct(a,bn))),
						vb=MathEqns.min(Math.acos(MathEqns.dotProduct(bn,b)),Math.PI-Math.acos(MathEqns.dotProduct(bn,b))),
						ab=MathEqns.min(Math.acos(MathEqns.dotProduct(a,b)),Math.PI-Math.acos(MathEqns.dotProduct(a,b)));
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
	if (type!=RATIO && type!=SUM && type!=DIFF && type!=DIFF) getNewXYZ(v1);
  }
}

class ProjectiveLine extends ProjectiveConstruct{
  double[] vec1={0,0,0}, vec2={0,0,0};
  double[] norm={0,0,0},binorm={0,0,0};
  public ProjectiveLine(int t, LinkedList<GeoConstruct> clickedList){super(t,clickedList);}
  public ProjectivePoint intersect(int m, ProjectiveConstruct a){
  	ProjectivePoint newPoint;
  	if(a.getType()==0)
  		newPoint=intersect(m,(ProjectiveCircle)a);
  	else
  		newPoint=intersect(m,(ProjectiveLine)a);
  	return newPoint;
  }
  public ProjectivePoint intersect(int m, ProjectiveLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	ProjectivePoint newPoint=new ProjectiveLINEintLINE(LINEintLINE0,tempList,v1);
  	return newPoint;
  }
  public ProjectivePoint intersect(int m, ProjectiveCircle a){
  	ProjectivePoint newPoint=a.intersect(m,this);
  	return newPoint;
  }

  public void draw(Graphics g, int SZ, boolean New) {
   if ((New && isRealNew) || (!New && isReal)) {
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	if(New){
      constList.get(0).getNewXYZ(vec1);	constList.get(1).getNewXYZ(vec2);
    }
    else{
      constList.get(0).getXYZ(vec1);	constList.get(1).getXYZ(vec2);
    }
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
    for (int i=0;i<333;i++) {
      v1[0]=vec1[0]*Math.cos(i/106.)+binorm[0]*Math.sin(i/106.);
      v1[1]=vec1[1]*Math.cos(i/106.)+binorm[1]*Math.sin(i/106.);
      v1[2]=vec1[2]*Math.cos(i/106.)+binorm[2]*Math.sin(i/106.);
      v2[0]=vec1[0]*Math.cos((i+1)/106.)+binorm[0]*Math.sin((i+1)/106.);
      v2[1]=vec1[1]*Math.cos((i+1)/106.)+binorm[1]*Math.sin((i+1)/106.);
      v2[2]=vec1[2]*Math.cos((i+1)/106.)+binorm[2]*Math.sin((i+1)/106.);
      if (v1[2]*v2[2]>=0) {
        MathEqns.normalize(v1);MathEqns.makeStandard(v1);
        MathEqns.normalize(v2);MathEqns.makeStandard(v2);
		switch (GeoPlayground.model) {
		  case 0:
		    MathEqns.makeStandard(v1);
			MathEqns.makeStandard(v2);
		    break;
		  case 1:
		  case 2:
		    for (int j=0;j<2;j++) {
			  v1[j]/=v1[2];
			  v2[j]/=v2[2];
			}
		    rescale(v1);rescale(v2);
		    break;
		}
        g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
                            SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
      }
      else if (getLabelShown() && GeoPlayground.model==0) 
	    g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
	  if (getLabelShown() && GeoPlayground.model>0) {
        for (int j=0;j<2;j++) {
		  v1[j]/=v1[2];
		  v2[j]/=v2[2];
		}
		if ((SZ+fudge+(int)(SZ*v1[0])<0 && SZ+fudge+(int)(SZ*v2[0])>=0) &&
            (SZ+(int)(SZ*v2[1])>=0 && SZ+(int)(SZ*v2[1])<=SZ*2))
          g.drawString(displayText,3,SZ+(int)(SZ*v2[1]));
        else if ((SZ+(int)(SZ*v1[1])<0 && SZ+(int)(SZ*v2[1])>=0) &&
                (SZ+fudge+(int)(SZ*v2[0])>=0 && SZ-fudge+(int)(SZ*v2[0])<=SZ*2))
          g.drawString(displayText,SZ+(int)(SZ*v2[0])+fudge,12);
        else if ((SZ-fudge+(int)(SZ*v1[0])>2*SZ && SZ-fudge+(int)(SZ*v2[0])<=2*SZ) &&
                (SZ+(int)(SZ*v2[1])>=0 && SZ+(int)(SZ*v2[1])<=SZ*2))
          g.drawString(displayText,2*SZ-18+2*fudge,SZ+(int)(SZ*v2[1]));
        else if ((SZ+(int)(SZ*v1[1])>2*SZ && SZ+(int)(SZ*v2[1])<=2*SZ) &&
                (SZ+(int)(SZ*v2[0])>=0 && SZ-fudge+(int)(SZ*v2[0])<=SZ*2))
          g.drawString(displayText,SZ+(int)(SZ*v2[0])+fudge,2*SZ-3);
	  }
    }
   }
  }  

  public boolean mouseIsOver(double[] v1, int SZ){
    double[] v2={0,0,0};
	getXYZ(v2);
	double[] v={v1[0],v1[1],v1[2]};
    unscale(v);
    double m=1+Math.sqrt(v[0]*v[0]+v[1]*v[1]);
    return Math.abs(Math.asin(MathEqns.dotProduct(v1,v2)))< 0.02/getScale()/m;
  }
  
  public void update() {
    double[] v1={0,0,0},v2={0,0,0};
    boolean nowValid=true;
	for (int i=0;i<getSize();i++) nowValid=(nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (nowValid){
	  constList.get(0).getNewXYZ(v1);
	  constList.get(1).getNewXYZ(v2);
	  if (type!=BISECTOR) setNewXYZ(v1,v2);
	}
  }
}

class ProjectiveCircle extends ProjectiveConstruct{
	double[] vec1={0,0,0}, vec2={0,0,0};
	double[] norm={0,0,0},binorm={0,0,0};
	public ProjectiveCircle(int t, LinkedList<GeoConstruct> clickedList) {
		super(t,clickedList);
		if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
	}
		
  public ProjectivePoint intersect(int m, ProjectiveConstruct a){
  	ProjectivePoint newPoint;
  	if(a.getType()==0)
  		newPoint=intersect(m,(ProjectiveCircle)a);
  	else
  		newPoint=intersect(m,(ProjectiveLine)a);
  	return newPoint;
  }

  public ProjectivePoint intersect(int m, ProjectiveLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	ProjectivePoint newPoint=new ProjectiveCIRCintLINE(CIRCintLINE0+m,tempList,v1);
  	newPoint.setXYZ(getCLxyz(newPoint,a,m));
  	return newPoint;
  }
  
  public ProjectivePoint intersect(int m, ProjectiveCircle a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	ProjectivePoint newPoint=new ProjectiveCIRCintCIRC(CIRCintCIRC00+m,tempList,v1);
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
	MathEqns.makeStandard(axis);														// 08_07_10
	MathEqns.makeStandard(p0);															// 08_07_10
	for (int i=0;i<3;i++) p1[i]=-p0[i];
	if (Math.acos(MathEqns.dotProduct(axis,p0))>Math.acos(MathEqns.dotProduct(axis,p1)))
	  for (int i=0;i<3;i++) p0[i]=p1[i];
	double d=MathEqns.dotProduct(axis,p0);
	for (int i=0;i<3;i++) p1[i]=p0[i]-d*axis[i];
	MathEqns.crossProduct(axis,p1,p2);
	MathEqns.normalize(p2);
	LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
	ProjectivePoint x0 = new ProjectivePoint(0,temp,axis);
	ProjectivePoint x1 = new ProjectivePoint(0,temp,p2);
	temp.add(x0); temp.add(x1);
	ProjectiveLine x2 = new ProjectiveLine(LINE,temp);
	x2.update(); x2.getNewXYZ(p3); x2.setXYZ(p3);
	ProjectivePoint x3;
	x3=((ProjectiveLine)x2).intersect(0,(ProjectiveCircle)this);
	x3.update(); x3.getNewXYZ(p3);
	
	d=Math.sqrt(MathEqns.dotProduct(p1,p1));
	for (int i=0;i<3;i++) p2[i]*=d;
	d=Math.sqrt(1-MathEqns.dotProduct(p2,p2));
	for (int i=0;i<710;i++) {
	  double[] v1={0,0,0},v2={0,0,0};
	  for (int j=0;j<3;j++) {
	    v1[j]=p1[j]*Math.cos(i/113.)+p2[j]*Math.sin(i/113.)+d*axis[j];
		v2[j]=p1[j]*Math.cos((i+1)/113.)+p2[j]*Math.sin((i+1)/113.)+d*axis[j];
	  }
      if (v1[2]*v2[2]>0) {
	    MathEqns.normalize(v1);MathEqns.makeStandard(v1);
        MathEqns.normalize(v2);MathEqns.makeStandard(v2);
        switch (GeoPlayground.model) {
		  case 0:
			break;
		  case 1:
		  case 2:
			if (v1[2]==0) v1[2]=.00000001;
			if (v2[2]==0) v2[2]=.00000001;
			v1[0]/=v1[2]; v1[1]/=v1[2];
			v2[0]/=v2[2]; v2[1]/=v2[2];
			rescale(v1);rescale(v2);
			break;
	    }
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
				   SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
      }
    }
	for (int j=0;j<3;j++) p2[j]=p2[j]+d*axis[j];
	if (getLabelShown()) {
	switch (GeoPlayground.model) {
	  case 0:
		break;
	  case 1:
	  case 2:
	    if (p3[2]==0) p3[2]=.00000001;
		p3[0]/=p3[2]; p3[1]/=p3[2];
		rescale(p3);
		break;
	}
	if (p3[2]>0)
	  g.drawString(displayText,SZ+(int)(SZ*p3[0])+fudge,SZ+(int)(SZ*p3[1]));
	else
	  g.drawString(displayText,SZ-(int)(SZ*p3[0])+fudge,SZ-(int)(SZ*p3[1]));
	}
   }
  }
  public boolean mouseIsOver(double[] mouse, int SZ){
    double[] axis={0,0,0}, point={0,0,0};
	constList.get(0).getXYZ(axis);
	MathEqns.makeStandard(axis);														// 08_07_10
	constList.get(1).getXYZ(point);
	MathEqns.makeStandard(point);														// 08_07_10
	double theta=Math.acos(MathEqns.dotProduct(axis,point));
	for (int i=0;i<3;i++) point[i]=-axis[i];
	double[] v={v1[0],v1[1],v1[2]};
    unscale(v);
    double m=1+Math.sqrt(v[0]*v[0]+v[1]*v[1]);
    if (Math.asin(axis[2])<theta)
	  if (Math.abs(Math.acos(MathEqns.dotProduct(axis,mouse))-theta)< 0.02/getScale()/m ||
	      Math.abs(Math.acos(MathEqns.dotProduct(point,mouse))-theta)< 0.02/getScale()/m) return true;
	  else return false;
    else return Math.abs(Math.acos(MathEqns.dotProduct(axis,mouse))-theta)< 0.02/getScale()/m;
  }
  
   public void update() {
     setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
  }
  
  public double[] getCLxyz(ProjectivePoint inter, ProjectiveConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getXYZ(u);					// a is a circle
	constList.get(1).getXYZ(v);					// b is a line
	b.getXYZ(w);					// we find a pt of intersection.
	if (i==0) inter.setValid(CircleEqns.calculateCL(u,v,w,x,true));
	else inter.setValid(CircleEqns.calculateCL(u,v,w,x,false));
	return x;
  }
  
  public double[] getNewCLxyz(ProjectivePoint inter,ProjectiveConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getNewXYZ(u);					// a is a circle
	constList.get(1).getNewXYZ(v);					// b is a line
	b.getNewXYZ(w);					// we find a pt of intersection.
	if (i==0) inter.isRealNew=CircleEqns.calculateCL(u,v,w,x,true);
	else inter.isRealNew=CircleEqns.calculateCL(u,v,w,x,false);
	return x;
  }
  
  public double[] getCCxyz(ProjectivePoint inter,ProjectiveConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getXYZ(t);					// a is a circle
	constList.get(1).getXYZ(u);					// b is a circle
	b.get(0).getXYZ(v);				// we find a pt of intersection.
	b.get(1).getXYZ(w);
	switch (i) {
	  case 0: inter.setValid(CircleEqns.calculateCC0(t,u,v,w,x,true));  break;
	  case 1: inter.setValid(CircleEqns.calculateCC0(t,u,v,w,x,false)); break;
	  case 2: inter.setValid(CircleEqns.calculateCC1(t,u,v,w,x,true));  break;
	  case 3: inter.setValid(CircleEqns.calculateCC1(t,u,v,w,x,false)); break;
	}
	return x;
  }
  public double[] getNewCCxyz(ProjectivePoint inter,ProjectiveConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
	constList.get(0).getNewXYZ(t);					// a is a circle
	constList.get(1).getNewXYZ(u);					// b is a circle
	b.get(0).getNewXYZ(v);			// we find a pt of intersection.
	b.get(1).getNewXYZ(w);
	switch (i) {
	  case 0: inter.isRealNew=CircleEqns.calculateCC0(t,u,v,w,x,true);  break;
	  case 1: inter.isRealNew=CircleEqns.calculateCC0(t,u,v,w,x,false); break;
	  case 2: inter.isRealNew=CircleEqns.calculateCC1(t,u,v,w,x,true);  break;
	  case 3: inter.isRealNew=CircleEqns.calculateCC1(t,u,v,w,x,false); break;
	}
	return x;
  }  
}

class ProjectivePTonLINE extends ProjectivePoint {
	public ProjectivePTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class ProjectivePTonCIRC extends ProjectivePoint {
	public ProjectivePTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}

class ProjectiveLINEintLINE extends ProjectivePoint {
	public ProjectiveLINEintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] nm={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		MathEqns.crossProduct(v1,v2,nm);
		MathEqns.normalize(nm);
		setValidNew(true);
		setNewXYZ(nm);
		for (int i=0;i<2;i++) if (constList.get(i).getType()==SEGMENT) {
			constList.get(i).get(0).getNewXYZ(v1);
			constList.get(i).get(1).getNewXYZ(v2);
			double	d0=Math.acos(MathEqns.dotProduct(v1,v2)),
					d1=Math.acos(MathEqns.dotProduct(v1,nm)),
					d2=Math.acos(MathEqns.dotProduct(nm,v2));
			d0=MathEqns.min(d0,Math.PI-d0);
			d1=MathEqns.min(d1,Math.PI-d1);
			d2=MathEqns.min(d2,Math.PI-d2);
			if (Math.abs(d0-d1-d2)>.0001)
				setValidNew(false);
		}
	}
}


class ProjectiveCIRCintLINE extends ProjectivePoint {
	public ProjectiveCIRCintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		setNewXYZ(((ProjectiveCircle)constList.get(0)).getNewCLxyz(this,(ProjectiveConstruct)constList.get(1),type-CIRCintLINE0));
		double[] nm={newX,newY,newZ};
		for (int i=0;i<2;i++) if (constList.get(i).getType()==SEGMENT) {
			constList.get(i).get(0).getNewXYZ(v1);
			constList.get(i).get(1).getNewXYZ(v2);
			double	d0=Math.acos(MathEqns.dotProduct(v1,v2)),
					d1=Math.acos(MathEqns.dotProduct(v1,nm)),
					d2=Math.acos(MathEqns.dotProduct(nm,v2));
			d0=MathEqns.min(d0,Math.PI-d0);
			d1=MathEqns.min(d1,Math.PI-d1);
			d2=MathEqns.min(d2,Math.PI-d2);
			if (Math.abs(d0-d1-d2)>.0001)
				setValidNew(false);
		}
	}
}

class ProjectiveCIRCintCIRC extends ProjectivePoint {
	public ProjectiveCIRCintCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		//super.update();
		setNewXYZ(((ProjectiveCircle)constList.get(0)).getNewCCxyz(this,(ProjectiveConstruct)constList.get(1),type-CIRCintCIRC00));
	}
}

class ProjectiveMIDPT extends ProjectivePoint {
	public ProjectiveMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] nm={0,0,0},bn={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		for (int i=0;i<3;i++) {nm[i]=(v1[i]+v2[i]);bn[i]=(v1[i]-v2[i]);}
		MathEqns.normalize(nm);
		MathEqns.normalize(bn);
		if (MathEqns.min(MathEqns.norm(nm,v1),MathEqns.norm(nm,v2))<
		    MathEqns.min(MathEqns.norm(bn,v1),MathEqns.norm(bn,v2)))
		  setNewXYZ(nm);
		else setNewXYZ(bn);
	}
}

class ProjectiveFIXedPT extends ProjectivePoint {
	public ProjectiveFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}
class ProjectiveReflectPt extends ProjectivePoint {
	public ProjectiveReflectPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).getNewXYZ(u);
		constList.get(1).getNewXYZ(v);
		tempList.add(constList.get(0).get(0));
		tempList.add(constList.get(0).get(1));
		ProjectiveLine l;
		if (constList.get(0).getType()==BISECTOR) {
			tempList.add(constList.get(0).get(2));
			l = new ProjectiveBISECTOR(BISECTOR,tempList);
		}
		else if (constList.get(0).getType()==PERP) l = new ProjectivePERP(PERP,tempList);
		else l = new ProjectiveLine(constList.get(0).getType(),tempList);
		l.update();		l.getNewXYZ(u);		l.setXYZ(u);	tempList.clear();
		ProjectivePoint p = new ProjectivePoint(POINT,tempList,v);
		// If the point is on the line of reflection,
		// the reflected point is just the point, hence
		if (Math.abs(MathEqns.dotProduct(u,v))<.000000001) setNewXYZ(v);
		else { // otherwise
			tempList.clear();
			tempList.add(l);
			tempList.add(p);
			ProjectivePERP a=new ProjectivePERP(PERP,tempList);
			a.update();		a.getNewXYZ(u);		a.setXYZ(u);
			ProjectivePoint b,x0,x1;
			b=((ProjectiveLine)a).intersect(0,(ProjectiveLine)l);
			b.update();		b.getNewXYZ(u);		b.setXYZ(u);
			tempList.clear();	tempList.add(b);	tempList.add(p);
			ProjectiveCircle c=new ProjectiveCircle(CIRCLE,tempList);
			x0=((ProjectiveLine)a).intersect(0,(ProjectiveCircle)c);
			x0.update();	x0.getNewXYZ(v);	x0.setXYZ(v);
			x1=((ProjectiveLine)a).intersect(1,(ProjectiveCircle)c);
			x1.update();	x1.getNewXYZ(w);	x1.setXYZ(w);
			p.getNewXYZ(u);
			if (MathEqns.norm(u,v)>MathEqns.norm(u,w)) setNewXYZ(v);
			else setNewXYZ(w);
		}
	}
}
class ProjectiveTranslatePt extends ProjectivePoint {
	public ProjectiveTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		ProjectivePoint a,b,c;
		constList.get(0).get(0).getNewXYZ(w);	a=new ProjectivePoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new ProjectivePoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			c=new ProjectivePoint(POINT,tempList,w);
		tempList.add(a);	tempList.add(b);
		ProjectiveLine d = new ProjectiveLine(LINE,tempList);
		d.update();			d.getNewXYZ(w);		d.setXYZ(w);
		ProjectiveMIDPT e = new ProjectiveMIDPT(MIDPT,tempList,w);
		e.update();			e.getNewXYZ(w);		e.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(a);
		ProjectivePERP f = new ProjectivePERP(PERP,tempList);
		f.update();			f.getNewXYZ(w);		f.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(e);
		ProjectivePERP g = new ProjectivePERP(PERP,tempList);
		g.update();			g.getNewXYZ(w);		g.setXYZ(w);
		tempList.clear();	tempList.add(f);	tempList.add(c);
		ProjectiveReflectPt h = new ProjectiveReflectPt(REFLECT_PT,tempList,w);
		h.update();			h.getNewXYZ(w);		h.setXYZ(w);
		tempList.clear();	tempList.add(g);	tempList.add(h);
		ProjectiveReflectPt i = new ProjectiveReflectPt(REFLECT_PT,tempList,w);
		i.update();			i.getNewXYZ(w);		setNewXYZ(w);
	}
}
class ProjectiveInvertPt extends ProjectivePoint {
	public ProjectiveInvertPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).get(0).getNewXYZ(u);
		constList.get(0).get(1).getNewXYZ(v);
		constList.get(1).getNewXYZ(w);
		if (MathEqns.norm(u,w)<.00001 || MathEqns.norm(u,v)>1.9999) setValidNew(false);
		else {
			setValidNew(true);
			double[] c0={0,0,0},c1={0,0,0},pt={0,0,0};
			u[0]*=-1;u[1]*=-1;
		    MathEqns.sphTranslate(u,v,c1);	MathEqns.makeStandard(c1);
			MathEqns.sphTranslate(u,w,pt);	MathEqns.makeStandard(pt);
			if (c1[2]==0) c1[2]=.00000001;
			if (pt[2]==0) pt[2]=.00000001;
			c1[0]/=c1[2];	c1[1]/=c1[2];	c1[2]=0;
			pt[0]/=pt[2];	pt[1]/=pt[2];	pt[2]=0;
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			EuclideanPoint ec0=new EuclideanPoint(POINT,tempList,c0);
			EuclideanPoint ec1=new EuclideanPoint(POINT,tempList,c1);
			EuclideanPoint ept=new EuclideanPoint(POINT,tempList,pt);
			tempList.add(ec0);	tempList.add(ec1);
			EuclideanCircle crc=new EuclideanCircle(CIRCLE,tempList);
			tempList.clear();	tempList.add(crc);	tempList.add(ept);
			EuclideanPoint inv=new EuclideanInvertPt(INVERT_PT,tempList,pt);
		    inv.update();	inv.getNewXYZ(pt);
		    pt[2]=1/Math.sqrt(pt[0]*pt[0]+pt[1]*pt[1]+1);
			pt[0]*=pt[2];
			pt[1]*=pt[2];
		    constList.get(0).get(0).getNewXYZ(u);
		    MathEqns.sphTranslate(u,pt,c0);	MathEqns.makeStandard(c0);
		    setNewXYZ(c0);
		}
	}
}
class ProjectiveRotatePt extends ProjectivePoint {
	public ProjectiveRotatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		ProjectivePoint a,b,c,d;
		constList.get(0).get(0).getNewXYZ(w);	a=new ProjectivePoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new ProjectivePoint(POINT,tempList,w);
		constList.get(0).get(2).getNewXYZ(w);	c=new ProjectivePoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			d=new ProjectivePoint(POINT,tempList,w);
		tempList.add(b);	tempList.add(a);
		ProjectiveLine l0 = new ProjectiveLine(LINE,tempList);
		l0.update();		l0.getNewXYZ(w);	l0.setXYZ(w);
		tempList.clear();	tempList.add(a);	tempList.add(b);	tempList.add(c);
		ProjectiveBISECTOR l1 = new ProjectiveBISECTOR(BISECTOR,tempList);
		l1.update();		l1.getNewXYZ(w);	l1.setXYZ(w);
		tempList.clear();	tempList.add(l0);	tempList.add(d);
		ProjectiveReflectPt p0 = new ProjectiveReflectPt(REFLECT_PT,tempList,w);
		p0.update();		p0.getNewXYZ(w);	p0.setXYZ(w);
		tempList.clear();	tempList.add(l1);	tempList.add(p0);
		ProjectiveReflectPt p1 = new ProjectiveReflectPt(REFLECT_PT,tempList,w);
		p1.update();		p1.getNewXYZ(w);	setNewXYZ(w);
	}
}
class ProjectiveMeasure extends ProjectivePoint {
    protected double[] a={0,0,0},b={0,0,0};
    double lengthOfRadius;
	public ProjectiveMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
		super(t, clickedList, v);
		setDisplayText();
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
      if ((New && getValidNew()) || (!New && getValid())) {
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);
		}
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
		lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
	  }
	}

}
class ProjectiveCOMMENT extends ProjectivePoint {
	public ProjectiveCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText,SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
}
class ProjectiveCONSTANT extends ProjectivePoint {
	public ProjectiveCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}

}
class ProjectiveSUM extends ProjectiveMeasure {
	public ProjectiveSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
        if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			if (!New) {
				if (getType()==SUM)		measureValue=get(0).measureValue+get(1).measureValue;
				if (getType()==DIFF)	measureValue=get(0).measureValue-get(1).measureValue;
				if (getType()==PROD)	measureValue=get(0).measureValue*get(1).measureValue;
				g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
			}
			else {
				if (getType()==SUM)		measureValueNew=get(0).measureValueNew+get(1).measureValueNew;
				if (getType()==DIFF)	measureValueNew=get(0).measureValueNew-get(1).measureValueNew;
				if (getType()==PROD)	measureValueNew=get(0).measureValueNew*get(1).measureValueNew;
				g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
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
class ProjectiveRATIO extends ProjectiveMeasure {
	public ProjectiveRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		update();
		if (!New) {
			if (get(1).measureValue!=0) {
				measureValue=get(0).measureValue/get(1).measureValue;
				g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
			}
		}
		else {
			if (get(1).measureValueNew!=0) {
				measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
				g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
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

class ProjectiveDISTANCE extends ProjectiveMeasure {
	public ProjectiveDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	g.drawString(displayText+"\u2248"
				   +MathEqns.chop(lengthOfRadius,GeoPlayground.digits),
				   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
			lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
			measureValueNew=lengthOfRadius;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
			lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
			measureValue=lengthOfRadius;
		}
	}
}

class ProjectiveTRIANGLE extends ProjectiveMeasure {
	public ProjectiveTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		double[] c={0,0,0},d={0,0,0};
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	double aa,bb,cc;
		  if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		  }
		  else {
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		  }
		  for (int i=0;i<3;i++) d[i]=-1*b[i];
		g.drawString(displayText+"\u2248"
					+MathEqns.chop(aa+bb+cc-Math.PI,GeoPlayground.digits),
					SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0},d={0,0,0};
			double aa,bb,cc;
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
		    get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		    if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
		    get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		    if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
			measureValueNew=aa+bb+cc-Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);	get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d))) for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d))) for (int i=0;i<3;i++) c[i]*=-1;
			cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		    measureValue=aa+bb+cc-Math.PI;
		}
	}
}
class ProjectiveANGLE extends ProjectiveMeasure {
	public ProjectiveANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	double[] c={0,0,0};
		if (New) {
		    get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
		}
		double[] d={-b[0],-b[1],-b[2]};
		if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d)))
			for (int i=0;i<3;i++) a[i]*=-1;
		if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d)))
			for (int i=0;i<3;i++) c[i]*=-1;
		if (GeoPlayground.degrees)
			g.drawString(displayText+"\u2248"
						       +MathEqns.chop(MathEqns.sphAngle(a,b,c),GeoPlayground.digits)+"\u00b0",
						       SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		else
			g.drawString(displayText+"\u2248"
				       +MathEqns.chop(MathEqns.sphAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits),
				       SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0},d={0,0,0};
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d)))
				for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d)))
				for (int i=0;i<3;i++) c[i]*=-1;
			measureValueNew=MathEqns.sphAngle(a,b,c);
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			if (Math.acos(MathEqns.dotProduct(a,b))>Math.acos(MathEqns.dotProduct(a,d)))
				for (int i=0;i<3;i++) a[i]*=-1;
			if (Math.acos(MathEqns.dotProduct(c,b))>Math.acos(MathEqns.dotProduct(c,d)))
				for (int i=0;i<3;i++) c[i]*=-1;
			measureValue=MathEqns.sphAngle(a,b,c);
			if (!GeoPlayground.degrees) {
				measureValueNew=measureValueNew/180.*Math.PI;
				measureValue=measureValue/180.*Math.PI;
			}
		}
	}
}

class ProjectiveCIRCUMF extends ProjectiveMeasure {
	public ProjectiveCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	g.drawString(displayText+"\u2248"
				   +MathEqns.chop(2*Math.PI*Math.sin(lengthOfRadius),GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
			lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
			measureValueNew=2*Math.sin(lengthOfRadius)*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
			lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
			measureValue=2*Math.sin(lengthOfRadius)*Math.PI;
		}
	}
}

class ProjectiveAREA extends ProjectiveMeasure {
	public ProjectiveAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    	g.drawString(displayText+"\u2248"
				   +MathEqns.chop((2-2*Math.cos(lengthOfRadius))*Math.PI,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
			lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
			measureValueNew=(2-2*Math.cos(lengthOfRadius))*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			lengthOfRadius=Math.acos(MathEqns.dotProduct(a,b));
			lengthOfRadius=MathEqns.min(lengthOfRadius,Math.PI-lengthOfRadius);
			measureValue=(2-2*Math.cos(lengthOfRadius))*Math.PI;
		}
	}
}

class ProjectivePERP extends ProjectiveLine {
	public ProjectivePERP(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}

class ProjectiveSEGMENT extends ProjectiveLine {
	public ProjectiveSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		double theta=Math.acos(MathEqns.dotProduct(vec1,vec2));
		  int sgn=-1;
		  if (theta>Math.PI/2+.0004) {sgn=1;theta=Math.PI-theta;}
		  double m=1024;
		  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	      if (GeoPlayground.model==1) m*=Math.sqrt(getScale());
			for (int i=0; i<theta*m;i++) {
			  v1[0]=vec1[0]*Math.cos(i/m)+sgn*binorm[0]*Math.sin(i/m);
	          v1[1]=vec1[1]*Math.cos(i/m)+sgn*binorm[1]*Math.sin(i/m);
	          v1[2]=vec1[2]*Math.cos(i/m)+sgn*binorm[2]*Math.sin(i/m);
	          v2[0]=vec1[0]*Math.cos((i+1)/m)+sgn*binorm[0]*Math.sin((i+1)/m);
	          v2[1]=vec1[1]*Math.cos((i+1)/m)+sgn*binorm[1]*Math.sin((i+1)/m);
	          v2[2]=vec1[2]*Math.cos((i+1)/m)+sgn*binorm[2]*Math.sin((i+1)/m);
			  if (sgn>0) {
			  if (v1[2]*v2[2]>0) {
	            MathEqns.normalize(v1);MathEqns.makeStandard(v1);
				MathEqns.normalize(v2);MathEqns.makeStandard(v2);
				switch (GeoPlayground.model) {
				  case 0:
					break;
				  case 1:
				  case 2:
					for (int j=0;j<2;j++) {
					  v1[j]/=v1[2];
					  v2[j]/=v2[2];
					}
					rescale(v1); rescale(v2);
					break;
				}
				g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
	                       SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
			  }
			  else // v1[2]*v2[2]<=0
			    if (getLabelShown()) {
				  if (v1[2]==0) v1[2]=.00000001;
				  if (v2[2]==0) v2[2]=.00000001;
				  switch (GeoPlayground.model) {
				    case 0:
					  break;
				    case 1:
				    case 2:
					  for (int j=0;j<2;j++) {
					    v1[j]/=v1[2];
					    v2[j]/=v2[2];
					  }
					  rescale(v1);rescale(v2);
					  break;
				  }
			      g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
			    }
			  }
			  else { // sgn<=0
			    if (v1[2]==0) v1[2]=.00000001;
				if (v2[2]==0) v2[2]=.00000001;
				switch (GeoPlayground.model) {
				    case 0:
					  break;
				    case 1:
				    case 2:
					  for (int j=0;j<2;j++) {
					    v1[j]/=v1[2];
					    v2[j]/=v2[2];
					  }
					  rescale(v1);rescale(v2);
					  break;
				  }
				if ((v1[0]-v2[0])*(v1[0]-v2[0])+(v1[1]-v2[1])*(v1[1]-v2[1])<1)
				g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
	                       SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
			    if (i>theta*m/3 && i<=theta*m/3+1 && getLabelShown() && sgn<0)
			      g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
			  }
			}
		  
	}
	public void update() {
		super.update();
	}
}

class ProjectiveBISECTOR extends ProjectiveLine {

	public ProjectiveBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] aa={0,0,0},bb={0,0,0},cc={0,0,0},mp={0,0,0},zz={0,0,0},yy={0,0,0};
		constList.get(0).getNewXYZ(yy);
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		ProjectivePoint y=new ProjectivePoint(POINT,tempList,yy);
		constList.get(1).getNewXYZ(bb);
		ProjectivePoint b=new ProjectivePoint(POINT,tempList,bb);
		constList.get(2).getNewXYZ(zz);
		ProjectivePoint z=new ProjectivePoint(POINT,tempList,zz);
		tempList.add(y); tempList.add(b);
		ProjectivePoint a=new ProjectiveMIDPT(MIDPT,tempList,bb);
		a.update();	a.getNewXYZ(aa);	a.setXYZ(aa);
		tempList.clear();	tempList.add(z); tempList.add(b);
		ProjectivePoint c=new ProjectiveMIDPT(MIDPT,tempList,bb);
		c.update();	c.getNewXYZ(cc);	c.setXYZ(cc);
		tempList.clear();	tempList.add(a);	tempList.add(c);
		ProjectivePoint m=new ProjectiveMIDPT(MIDPT,tempList,mp);
		m.update();	m.getNewXYZ(mp);	m.setXYZ(mp);
		if (Math.abs(MathEqns.determinant(aa,bb,cc))<.000001) {
			if (MathEqns.sphAngle(aa,bb,cc)>175) {
				tempList.clear();	tempList.add(a);	tempList.add(c);
				ProjectiveCircle d=new ProjectiveCircle(CIRCLE,tempList);
				tempList.clear();	tempList.add(c);	tempList.add(a);
				ProjectiveCircle e=new ProjectiveCircle(CIRCLE,tempList);
				ProjectivePoint f1,f2;
				f1=((ProjectiveCircle)d).intersect(0,(ProjectiveCircle)e);
				f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
				f2=((ProjectiveCircle)d).intersect(1,(ProjectiveCircle)e);
				f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
				if (!f1.getValid()) {
					f1=((ProjectiveCircle)d).intersect(2,(ProjectiveCircle)e);
					f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
					f2=((ProjectiveCircle)d).intersect(3,(ProjectiveCircle)e);
					f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
				}
				tempList.clear();	tempList.add(f1);	tempList.add(f2);
			}
			else {
				tempList.clear();	tempList.add(a);	tempList.add(b);
			}
		}
		else {
			tempList.clear();	tempList.add(b);	tempList.add(c);
			ProjectiveLine d=new ProjectiveLine(LINE,tempList);
			d.update();	d.getNewXYZ(v1);
			tempList.clear();	tempList.add(b);	tempList.add(a);
			ProjectiveCircle e=new ProjectiveCircle(CIRCLE,tempList);
			ProjectivePoint f1,f2;
			f1=((ProjectiveLine)d).intersect(0,(ProjectiveCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((ProjectiveLine)d).intersect(1,(ProjectiveCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(f1);
			ProjectivePoint g1=new ProjectiveMIDPT(MIDPT,tempList,v1);
			g1.update();	g1.getNewXYZ(v1);	g1.setXYZ(v1);
			tempList.clear();	tempList.add(a);	tempList.add(f2);
			ProjectivePoint g2=new ProjectiveMIDPT(MIDPT,tempList,v2);
			g2.update();	g2.getNewXYZ(v2);	g2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(c);
			ProjectivePoint g3=new ProjectiveMIDPT(MIDPT,tempList,bb);
			g3.update();	g3.getNewXYZ(bb);	g3.setXYZ(bb);
			if (Math.abs(MathEqns.dotProduct(v1,bb))<Math.abs(MathEqns.dotProduct(v2,bb)))
				g1=g2;
			tempList.clear();	tempList.add(b);	tempList.add(g1);
		}
		ProjectiveLine h=new ProjectiveLine(LINE,tempList);
		h.update();	h.getNewXYZ(v1);
		if (MathEqns.norm(aa,bb)*MathEqns.norm(cc,bb)>.000000001) setNewXYZ(v1);
		else setValidNew(false);
	}
}
