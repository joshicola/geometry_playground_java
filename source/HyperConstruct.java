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

abstract class HyperConstruct extends GeoConstruct {
  protected static double scale=0;//Powers of two
  public static final int scaleLimit=5;
  protected double[] v1={0,0,0},v2={0,0,0};
  
  public HyperConstruct(int t, LinkedList<GeoConstruct> clickedList){
	  type=t;
	  for(int i=0;i<clickedList.size();i++)
		  constList.addLast(clickedList.get(i));
	  if (constList.size()==2) {
	    double[] v1={0,0,0}, v2={0,0,0};
        constList.get(0).getXYZ(v1);
        constList.get(1).getXYZ(v2);
        this.setXYZ(v1, v2);
	  }
  }
  public HyperConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v1){
	  type=t;
	  for(int i=0;i<clickedList.size();i++)
		  constList.addLast(clickedList.get(i));
	  if (t==ANGLE) {
		    double[] v2={0,0,0};
			constList.get(0).getXYZ(v1);
			constList.get(2).getXYZ(v2);
			for (int i=0;i<3;i++) v1[i]=(v1[i]+v2[i])/2; // This doesn't work for midpoint in Hyper...
	  }
	  if (t==DISTANCE || t==RATIO || t==SUM || t==DIFF || t==PROD) {
		double[] v2={0,0,0};
		get(0).getXYZ(v1);
		get(1).getXYZ(v2);
		for (int i=0;i<3;i++) v1[i]=(v1[i]+v2[i])/2; // This doesn't work for midpoint in Hyper...
	  }
	  if (t==TRIANGLE) {
		  double[] v2={0,0,0},v3={0,0,0};
			get(0).getXYZ(v1);
			get(1).getXYZ(v2);
			get(2).getXYZ(v3);
			for (int i=0;i<3;i++) v1[i]=(v1[i]+v2[i]+v3[i])/3;
	  }
	  if (t==CONSTANT) {
		  measureValue=v1[2];
		  measureValueNew=v1[2];
		  v1[2]=Math.sqrt(1+v1[0]*v1[0]+v1[1]*v1[1]);
	  }
	  setXYZ(v1);
	  update();
  }
  
  public abstract HyperPoint intersect(int m, HyperConstruct a);

  public void setXYZ(double[] vector) {
    if (vector[0]==0 && vector[1]==0) vector[0]+=.00000001;
    MathEqns.hypNormalize(vector);
    MathEqns.makeStandard(vector);
    x=vector[0]; y=vector[1]; z=vector[2];
	newX=x;		newY=y;		newZ=z;
  }
  public void setXYZ(double[] v1, double[] v2) {
    double[] normal={v1[1]*v2[2]-v2[1]*v1[2],
        v2[0]*v1[2]-v1[0]*v2[2],
        v1[0]*v2[1]-v2[0]*v1[1]}; // normal vector
    if (MathEqns.hypProduct(normal,normal)*getType()>0) {setXYZ(normal);setValid(true);}
    else setValid(false);
  }
  public void setNewXYZ(double[] vector) {
    MathEqns.makeStandard(vector);
    newX=vector[0]; newY=vector[1]; newZ=vector[2];
  }
  public void setNewXYZ(double[] v1, double[] v2) {
    double[] normal={v1[1]*v2[2]-v2[1]*v1[2],
        v2[0]*v1[2]-v1[0]*v2[2],
        v1[0]*v2[1]-v2[0]*v1[1]}; // normal vector
	if (MathEqns.hypProduct(normal,normal)*getType()>0) {setNewXYZ(normal);setValidNew(true);}
    else setValidNew(false);
  }

  public void translate(double[] dragStart, double[] dragNow){
    double[] v1={0,0,0},v2={0,0,0},nm={0,0,0};
    this.getXYZ(v1);
    MathEqns.hypTranslate(dragStart,v1,v2);
    for (int j=0;j<2;j++) nm[j]=-1*dragNow[j];
    nm[2]=dragNow[2];
    MathEqns.hypTranslate(nm,v2,v1);
    this.setNewXYZ(v1);
  }
  public static double getScale(){
    return Math.exp(scale*Math.log(2));
  }
  public static void setScale(double s){
  	if(GeoPlayground.model==3)
	  if (s*s<.00000001) resetScale();
      else if(Math.abs(scale+s)<=scaleLimit)
        scale+=s;
  }
  public static void resetScale(){scale=0;}
  public static void rescale(double[] v){
    v[0]*=getScale();
    v[1]*=getScale();
  }
  public static void unscale(double[] v){
    v[0]/=getScale();
    v[1]/=getScale();    
  }
  public void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow) {
    MathEqns.transform(fixedObject,this,dragStart,dragNow);
  }
} // end class 


class HyperPoint extends HyperConstruct{
  protected int sz;
  public HyperPoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}

  public HyperPoint intersect(int m, HyperConstruct a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	HyperPoint newPoint=new HyperPoint(LINEintLINE0,tempList,v1);
    return newPoint;
  }

  public void draw(Graphics g, int SZ, boolean New) {
    sz=(int)MathEqns.max(4,SZ/40);
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	if ((New && isRealNew) || (!New && isReal)) { 
      if(New) getNewXYZ(v1);
      else getXYZ(v1);
      switch(GeoPlayground.model) {
	    case 3:
		  rescale(v1);
		  break;
		case 0:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  v1[0]/=(1+v1[2]);
		  v1[1]/=(1+v1[2]);
		  break;
		case 1:
		  if (v1[2]==0) v1[2]=.00000001;
		  v1[0]/=v1[2];
		  v1[1]/=v1[2];
		  break;
		case 2:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  v1[0]/=(1+v1[2]);
		  v1[1]/=(1+v1[2]);
		  double xx=v1[0],yy=v1[1];
		  v1[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  v1[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  break;
	  }
	  if (getType()<30) {
	    g.fillOval(SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2,sz,sz);
		if (getLabelShown())
          g.drawString(displayText,SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2);
	  }
    }
  }

  public boolean mouseIsOver(double[] v1, int SZ){
    double sz=MathEqns.max(6,SZ/20);
    double xx=x,yy=y,zz=z,m=1;
	switch(GeoPlayground.model) {
	  case 3:
		break;
	  case 0:
		if (zz==-1) zz=-.99999999;
		xx/=(1+zz);	yy/=(1+zz);
		m=2/(1-xx*xx-yy*yy);
		break;
	  case 1:
		if (zz==0) zz=.00000001;
		xx/=zz;		yy/=zz;
		m=1/Math.sqrt(1-xx*xx-yy*yy);
		break;
	  case 2:
		if (zz==-1) zz=-.99999999;
		xx/=(1+zz);	yy/=(1+zz);
		yy=-4*(yy+1)/(xx*xx+(yy+1)*(yy+1))+3;
		m=-1/(yy-1);
	  break;
	}
	return ((x-v1[0])*(x-v1[0])+(y-v1[1])*(y-v1[1])<sz*sz/(SZ*SZ)/(getScale()*getScale())*m*m); 
  }

  public void updatePTonLINE() {
	  double[] v1={0,0,0},v2={0,0,0},nm={0,0,0};
	  setValidNew(get(0).getValidNew());
	  if (getValidNew()) {
		  getNewXYZ(v1);
		  constList.get(0).getNewXYZ(v2);
		  MathEqns.hypPerp(v2,v1,nm);
		  MathEqns.hypLineIntLine(v2,nm,v1);
		  if (constList.get(0).getType()==SEGMENT) {
				double[] a={0,0,0},b={0,0,0};
				constList.get(0).get(0).getNewXYZ(a);
				constList.get(0).get(1).getNewXYZ(b);
				double 	av=MathEqns.acosh(Math.abs(MathEqns.hypProduct(a,v1))),
						vb=MathEqns.acosh(Math.abs(MathEqns.hypProduct(v1,b))),
						ab=MathEqns.acosh(Math.abs(MathEqns.hypProduct(a,b)));
				if (Math.abs(av+vb-ab)>.0001) {
					if (av>vb) constList.get(0).get(1).getNewXYZ(v1);
					else constList.get(0).get(0).getNewXYZ(v1);
				}
		  }
		  if (constList.get(0).getType()==RAY) {
				double[] a={0,0,0},b={0,0,0};
				constList.get(0).get(0).getNewXYZ(a);
				constList.get(0).get(1).getNewXYZ(b);
				double 	av=MathEqns.acosh(Math.abs(MathEqns.hypProduct(a,v1))),
						vb=MathEqns.acosh(Math.abs(MathEqns.hypProduct(v1,b))),
						ab=MathEqns.acosh(Math.abs(MathEqns.hypProduct(a,b)));
				if (Math.abs(av+ab-vb)<.0001) {
					constList.get(0).get(0).getNewXYZ(v1);
				}
		  }
		  
		  setNewXYZ(v1);
	  }
  }
  public void updatePTonCIRC() {
	  double[] v1={0,0,0},v2={0,0,0},v3={0,0,0},nm={0,0,0},bn={0,0,0};
	  setValidNew(get(0).getValidNew());
	  if (getValidNew()) {
		  getNewXYZ(v1);
		  constList.get(0).get(0).getNewXYZ(v2);
		  constList.get(0).get(1).getNewXYZ(v3);
		  MathEqns.crossProduct(v1,v2,nm);
		  CircleEqns.calculateHypCL(v2,v3,nm,bn,true);
		  double phi=MathEqns.hypProduct(bn,v1);
		  setNewXYZ(bn);
		  CircleEqns.calculateHypCL(v2,v3,nm,bn,false);
		  double psi=MathEqns.hypProduct(bn,v1);
		  if(phi-psi>0) setNewXYZ(bn);
		  setValidNew(get(0).getValidNew());
		}
  }
  
  public void update() {
	boolean nowValid=true;
	for (int i=0;i<constList.size();i++) nowValid = (nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (type!=RATIO && type!=SUM && type!=DIFF && type!=PROD) getNewXYZ(v1);
  }
}

class HyperLine extends HyperConstruct{
  double[] v1={0,0,0}, v2={0,0,0}, nm={0,0,0};
  public HyperLine(int t, LinkedList<GeoConstruct> clickedList){super(t,clickedList);}
  public HyperPoint intersect(int m, HyperConstruct a){
    HyperPoint newPoint;
    if(a.getType()==0)
      newPoint=intersect(m,(HyperCircle)a);
    else
      newPoint=intersect(m,(HyperLine)a);
    return newPoint;
  }
  public HyperPoint intersect(int m, HyperLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(a);	tempList.add(this);
	HyperPoint newPoint=new HyperLINEintLINE(LINEintLINE0,tempList,v1);
    return newPoint;
  }
  public HyperPoint intersect(int m, HyperCircle a){
    HyperPoint newPoint=a.intersect(m,this);
    return newPoint;
  }
  public void draw(Graphics g, int SZ, boolean New) {
    if ((New && isRealNew) || (!New && isReal)) {
      int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
      if (type==SEGMENT) {
	    if (New){ constList.get(0).getNewXYZ(v1);	constList.get(1).getNewXYZ(v2); }
		else	{ constList.get(0).getXYZ(v1);		constList.get(1).getXYZ(v2); }
		MathEqns.hypCrossProduct(v1,v2,nm);
        double	a=nm[0],	b=nm[1],	c=nm[2];
        double s1=Math.sqrt(a*a+b*b),	s2=Math.sqrt(a*a+b*b-c*c);
        // finding the "natural" parameter endpoints t1 and t2.
		double t1=Math.log(v1[2]*s2/s1+Math.sqrt(v1[2]*v1[2]*s2*s2/s1/s1-1));
		double t2=Math.log(v2[2]*s2/s1+Math.sqrt(v2[2]*v2[2]*s2*s2/s1/s1-1));
        // since the inverse hyperbolic cosine is double-valued, we do
		// the next few steps to make sure we have the correct values
        if ((Math.abs((-a*c*Math.cosh(t1)/s2+b*Math.sinh(t1))/s1-v1[0])>.00001 && Math.abs(v1[1]-v2[1])>.00001)
            || (Math.abs((-b*c*Math.cosh(t1)/s2-a*Math.sinh(t1))/s1-v1[1])>.00001 && Math.abs(v1[0]-v2[0])>.00001))
          t1=Math.log(v1[2]*s2/s1-Math.sqrt(v1[2]*v1[2]*s2*s2/s1/s1-1));
        if ((Math.abs((-a*c*Math.cosh(t2)/s2+b*Math.sinh(t2))/s1-v2[0])>.00001 && Math.abs(v1[1]-v2[1])>.00001)
            || (Math.abs((-b*c*Math.cosh(t2)/s2-a*Math.sinh(t2))/s1-v2[1])>.00001 && Math.abs(v1[0]-v2[0])>.00001))
          t2=Math.log(v2[2]*s2/s1-Math.sqrt(v2[2]*v2[2]*s2*s2/s1/s1-1));
        if (t2<t1) {
          double temp=t1; t1=t2; t2=temp;
        }
        for (double i=t1; i<t2-(t2-t1)/64; i+=(t2-t1)/32) {
          v1[0]=(-a*c/s2*Math.cosh(i)+b*Math.sinh(i))/s1;
          v1[1]=(-b*c/s2*Math.cosh(i)-a*Math.sinh(i))/s1;
          v1[2]=s1/s2*Math.cosh(i);
          v2[0]=(-a*c/s2*Math.cosh(i+(t2-t1)/32)+b*Math.sinh(i+(t2-t1)/32))/s1;
          v2[1]=(-b*c/s2*Math.cosh(i+(t2-t1)/32)-a*Math.sinh(i+(t2-t1)/32))/s1;
          v2[2]=s1/s2*Math.cosh(i+(t2-t1)/32);
          switch(GeoPlayground.model) {
	    case 3:
		  break;
		case 0:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  if (v2[2]==-1) v2[2]=-.99999999;
		  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
		  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
		  break;
		case 1:
		  if (v1[2]==0) v1[2]=.00000001;
		  if (v2[2]==0) v2[2]=.00000001;
		  v1[0]/=v1[2];	v1[1]/=v1[2];
		  v2[0]/=v2[2];	v2[1]/=v2[2];
		  break;
		case 2:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  if (v2[2]==-1) v2[2]=-.99999999;
		  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
		  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
		  double xx=v1[0],yy=v1[1];
		  v1[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  v1[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  xx=v2[0];yy=v2[1];
		  v2[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  v2[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  break;
	  }
		  g.drawLine(SZ+(int)(SZ*v1[0]*getScale())+fudge,SZ+(int)(SZ*v1[1]*getScale()),
              SZ+(int)(SZ*v2[0]*getScale())+fudge,SZ+(int)(SZ*v2[1]*getScale()));
		  if (i>=(t2+t1)/2 && i<(t2+t1)/2+(t2-t1)/32 && getLabelShown())
		  g.drawString(displayText,
		      SZ+(int)(SZ*v2[0]*getScale())+fudge,SZ+(int)(SZ*v2[1]*getScale()));
        }
	  }
	  else if (type==RAY) {
		    if (New){ constList.get(0).getNewXYZ(v1);	constList.get(1).getNewXYZ(v2); }
			else	{ constList.get(0).getXYZ(v1);		constList.get(1).getXYZ(v2); }
			MathEqns.hypCrossProduct(v1,v2,nm);
	        double	a=nm[0],	b=nm[1],	c=nm[2];
	        double s1=Math.sqrt(a*a+b*b),	s2=Math.sqrt(a*a+b*b-c*c);
	        // finding the "natural" parameter endpoints t1 and t2.
			double t1=Math.log(v1[2]*s2/s1+Math.sqrt(v1[2]*v1[2]*s2*s2/s1/s1-1));
			double u2=Math.log(v2[2]*s2/s1+Math.sqrt(v2[2]*v2[2]*s2*s2/s1/s1-1));
	        // since the inverse hyperbolic cosine is double-valued, we do
			// the next few steps to make sure we have the correct values
	        if ((Math.abs((-a*c*Math.cosh(t1)/s2+b*Math.sinh(t1))/s1-v1[0])>.00001 && Math.abs(v1[1]-v2[1])>.00001)
	            || (Math.abs((-b*c*Math.cosh(t1)/s2-a*Math.sinh(t1))/s1-v1[1])>.00001 && Math.abs(v1[0]-v2[0])>.00001))
	          t1=Math.log(v1[2]*s2/s1-Math.sqrt(v1[2]*v1[2]*s2*s2/s1/s1-1));
	        if ((Math.abs((-a*c*Math.cosh(u2)/s2+b*Math.sinh(u2))/s1-v2[0])>.00001 && Math.abs(v1[1]-v2[1])>.00001)
	            || (Math.abs((-b*c*Math.cosh(u2)/s2-a*Math.sinh(u2))/s1-v2[1])>.00001 && Math.abs(v1[0]-v2[0])>.00001))
	          u2=Math.log(v2[2]*s2/s1-Math.sqrt(v2[2]*v2[2]*s2*s2/s1/s1-1));
	        double t2;
	        if (u2<t1) t2=-10; // since we want to go out to infinity, 
	        else t2=10;        // choose the second endpoint really big (e^10)
	        for (double i=t1; i<t2-(t2-t1)/64; i+=(t2-t1)/256) {
	          v1[0]=(-a*c/s2*Math.cosh(i)+b*Math.sinh(i))/s1;
	          v1[1]=(-b*c/s2*Math.cosh(i)-a*Math.sinh(i))/s1;
	          v1[2]=s1/s2*Math.cosh(i);
	          v2[0]=(-a*c/s2*Math.cosh(i+(t2-t1)/256)+b*Math.sinh(i+(t2-t1)/256))/s1;
	          v2[1]=(-b*c/s2*Math.cosh(i+(t2-t1)/256)-a*Math.sinh(i+(t2-t1)/256))/s1;
	          v2[2]=s1/s2*Math.cosh(i+(t2-t1)/256);
	          switch(GeoPlayground.model) {
		    case 3:
			  break;
			case 0:
			  if (v1[2]==-1) v1[2]=-.99999999;
			  if (v2[2]==-1) v2[2]=-.99999999;
			  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
			  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
			  break;
			case 1:
			  if (v1[2]==0) v1[2]=.00000001;
			  if (v2[2]==0) v2[2]=.00000001;
			  v1[0]/=v1[2];	v1[1]/=v1[2];
			  v2[0]/=v2[2];	v2[1]/=v2[2];
			  break;
			case 2:
			  if (v1[2]==-1) v1[2]=-.99999999;
			  if (v2[2]==-1) v2[2]=-.99999999;
			  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
			  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
			  double xx=v1[0],yy=v1[1];
			  v1[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
			  v1[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
			  xx=v2[0];yy=v2[1];
			  v2[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
			  v2[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
			  break;
		  }
			  g.drawLine(SZ+(int)(SZ*v1[0]*getScale())+fudge,SZ+(int)(SZ*v1[1]*getScale()),
	              SZ+(int)(SZ*v2[0]*getScale())+fudge,SZ+(int)(SZ*v2[1]*getScale()));
			  if (i>=(u2+t1)/2 && i<(u2+t1)/2+(t2-t1)/256 && getLabelShown())
			  g.drawString(displayText,
			      SZ+(int)(SZ*v2[0]*getScale())+fudge,SZ+(int)(SZ*v2[1]*getScale()));
	        }
		  }
	  else { // type=LINE or PERP or BISECTOR or PARLL-0or1
	    if(New) getNewXYZ(nm);
        else getXYZ(nm);
		double a=nm[0],b=nm[1],c=nm[2];
		double s1=Math.sqrt(a*a+b*b), s2=Math.sqrt(a*a+b*b-c*c);
		for (int i=-343; i<343; i++) {
          v1[0]=(-a*c/s2*Math.cosh(Math.tan(i/219.))+b*Math.sinh(Math.tan(i/219.)))/s1;
          v1[1]=(-b*c/s2*Math.cosh(Math.tan(i/219.))-a*Math.sinh(Math.tan(i/219.)))/s1;
          v1[2]=s1/s2*Math.cosh(Math.tan(i/219.));
          v2[0]=(-a*c/s2*Math.cosh(Math.tan((i+1)/219.))+b*Math.sinh(Math.tan((i+1)/219.)))/s1;
          v2[1]=(-b*c/s2*Math.cosh(Math.tan((i+1)/219.))-a*Math.sinh(Math.tan((i+1)/219.)))/s1;
          v2[2]=s1/s2*Math.cosh(Math.tan((i+1)/219.));
		  switch(GeoPlayground.model) {
	    case 3:
		  break;
		case 0:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  if (v2[2]==-1) v2[2]=-.99999999;
		  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
		  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
		  break;
		case 1:
		  if (v1[2]==0) v1[2]=.00000001;
		  if (v2[2]==0) v2[2]=.00000001;
		  v1[0]/=v1[2];	v1[1]/=v1[2];
		  v2[0]/=v2[2];	v2[1]/=v2[2];
		  break;
		case 2:
		  if (v1[2]==-1) v1[2]=-.99999999;
		  if (v2[2]==-1) v2[2]=-.99999999;
		  v1[0]/=(1+v1[2]);	v1[1]/=(1+v1[2]);
		  v2[0]/=(1+v2[2]);	v2[1]/=(1+v2[2]);
		  double xx=v1[0],yy=v1[1];
		  v1[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  v1[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  xx=v2[0];yy=v2[1];
		  v2[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  v2[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  v1[2]=0;v2[2]=0;
		  break;
	  }
          g.drawLine(SZ+(int)(SZ*v1[0]*getScale())+fudge,SZ+(int)(SZ*v1[1]*getScale()),
        			 SZ+(int)(SZ*v2[0]*getScale())+fudge,SZ+(int)(SZ*v2[1]*getScale()));
          if (getLabelShown() && i==3)
        	  g.drawString(displayText,SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]));
        }
      }
	}
  }  

  public boolean mouseIsOver(double[] mouse, int SZ){
    double[] v2={0,0,0};
	getXYZ(v2);
	return Math.abs(Math.asin(MathEqns.dotProduct(mouse,v2)))< 0.02/getScale();
  }
  public void update() {
    double[] v1={0,0,0},v2={0,0,0};
	boolean nowValid=true;
	for (int i=0;i<getSize();i++) nowValid=(nowValid && constList.get(i).getValidNew());
	setValidNew(nowValid);
	if (nowValid) {
		if (getType()==LINE || getType()==SEGMENT || getType()==RAY) {
		  constList.get(0).getNewXYZ(v1);
          constList.get(1).getNewXYZ(v2);
		  if (MathEqns.norm(v1,v2)<.0001) setValidNew(false);
          else setNewXYZ(v1,v2);
	    }
	}
  }
}

class HyperCircle extends HyperConstruct{
	public HyperCircle(int t, LinkedList<GeoConstruct> clickedList) {
		super(t,clickedList);
		if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
	}
	public HyperPoint intersect(int m, HyperConstruct a){
    HyperPoint newPoint;
    if(a.getType()==0)
      newPoint=intersect(m,(HyperCircle)a);
    else
      newPoint=intersect(m,(HyperLine)a);
    return newPoint;
  }

  public HyperPoint intersect(int m, HyperLine a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
    HyperPoint newPoint=new HyperCIRCintLINE(CIRCintLINE0+m,tempList,v1);
    newPoint.setXYZ(getCLxyz(newPoint,a,m));
    return newPoint;
  }

  public HyperPoint intersect(int m, HyperCircle a){
	LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
	tempList.add(this);	tempList.add(a);
	HyperPoint newPoint=new HyperCIRCintCIRC(CIRCintCIRC00+m,tempList,v1);
    newPoint.setXYZ(getCCxyz(newPoint,a,m));
    return newPoint;
  }

  public void draw(Graphics g, int SZ, boolean New) {
    if ((New && isRealNew) || (!New && isReal)) {
      double[] axis={0,0,0},	p={0,0,0},	pTranslate={0,0,0},
      v1={0,0,0},	v2={0,0,0},
      u1={0,0,0},	u2={0,0,0};	
      if(New){ constList.get(0).getNewXYZ(axis);	constList.get(1).getNewXYZ(p); }
      else	{ constList.get(0).getXYZ(axis);		constList.get(1).getXYZ(p); }
      MathEqns.hypTranslate(axis,p,pTranslate);
      axis[0]*=-1; axis[1]*=-1;
      double r=Math.sqrt(pTranslate[0]*pTranslate[0]+pTranslate[1]*pTranslate[1]);
	  int temp=MathEqns.round(MathEqns.min(MathEqns.max(r/2,1),4096));
	  int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	  for (int i=0;i<44*temp;i++) {
        v1[0]=r*Math.cos(i/(7.*temp));				v2[0]=r*Math.cos((i+1)/(7.*temp));
        v1[1]=r*Math.sin(i/(7.*temp));				v2[1]=r*Math.sin((i+1)/(7.*temp));
        v1[2]=Math.sqrt(1+v1[0]*v1[0]+v1[1]*v1[1]);	v2[2]=Math.sqrt(1+v1[0]*v1[0]+v1[1]*v1[1]);
        MathEqns.hypTranslate(axis,v1,u1);			MathEqns.hypTranslate(axis,v2,u2);
        switch (GeoPlayground.model) {
		  case 3:
		    break;
		  case 0:
		    if (u1[2]==-1) u1[2]=-.99999999;
		    if (u2[2]==-1) u2[2]=-.99999999;
		    u1[0]/=(1+u1[2]);	u1[1]/=(1+u1[2]);
		    u2[0]/=(1+u2[2]);	u2[1]/=(1+u2[2]);
		    break;
		  case 1:
		    if (u1[2]==0) u1[2]=.00000001;
		    if (u2[2]==0) u2[2]=.00000001;
		    u1[0]/=u1[2];	u1[1]/=u1[2];
		    u2[0]/=u2[2];	u2[1]/=u2[2];
		    break;
		  case 2:
		    if (u1[2]==-1) u1[2]=-.99999999;
		    if (u2[2]==-1) u2[2]=-.99999999;
		    u1[0]/=(1+u1[2]);	u1[1]/=(1+u1[2]);
		    u2[0]/=(1+u2[2]);	u2[1]/=(1+u2[2]);
		    double xx=u1[0],yy=u1[1];
		  u1[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  u1[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  xx=u2[0];yy=u2[1];
		  u2[0]=2*xx/(xx*xx+(yy+1)*(yy+1));
		  u2[1]=-2*(yy+1)/(xx*xx+(yy+1)*(yy+1))+2;
		  
			break;
		}
		g.drawLine(SZ+(int)(SZ*u1[0]*getScale())+fudge,SZ+(int)(SZ*u1[1]*getScale()),SZ+(int)(SZ*u2[0]*getScale())+fudge,SZ+(int)(SZ*u2[1]*getScale()));
		if (i==22 && getLabelShown())
		  g.drawString(displayText,
		    SZ+(int)(SZ*u1[0]*getScale())+fudge,SZ+(int)(SZ*u1[1]*getScale()));
      }
    }
  }
  public boolean mouseIsOver(double[] mouse, int SZ){
    double[] axis={0,0,0}, point={0,0,0};
    constList.get(0).getXYZ(axis);
    constList.get(1).getXYZ(point);
    double theta=MathEqns.acosh(MathEqns.hypProduct(axis,point));
    if (Math.abs(MathEqns.acosh(MathEqns.hypProduct(axis,mouse))-theta)< 0.02/getScale()) return true;
    else return false;
  }

  public void update() {
    setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
  }

  public double[] getCLxyz(HyperPoint inter, HyperConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getXYZ(u);					// a is a circle w/ ctr A & pt B
    constList.get(1).getXYZ(v);					// b is a line
    b.getXYZ(w);					// we find a pt of intersection.
    inter.setValid(CircleEqns.calculateHypCL(u,v,w,x,(i==0)));
    return x;
  }

  public double[] getNewCLxyz(HyperPoint inter,HyperConstruct b, int i) {
    double[] u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getNewXYZ(u);					// a is a circle
    constList.get(1).getNewXYZ(v);					// b is a line
    b.getNewXYZ(w);					// we find a pt of intersection.
    inter.isRealNew=CircleEqns.calculateHypCL(u,v,w,x,(i==0));
    return x;
  }

  public double[] getCCxyz(HyperPoint inter,HyperConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getXYZ(t);					// a is a circle
    constList.get(1).getXYZ(u);					// b is a circle
    b.get(0).getXYZ(v);				// we find a pt of intersection.
    b.get(1).getXYZ(w);
    inter.setValid(CircleEqns.calculateHypCC(t,u,v,w,x,(i==0)));
    return x;
  }
  public double[] getNewCCxyz(HyperPoint inter,HyperConstruct b, int i) {
    double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
    constList.get(0).getNewXYZ(t);					// a is a circle
    constList.get(1).getNewXYZ(u);					// b is a circle
    b.get(0).getNewXYZ(v);			// we find a pt of intersection.
    b.get(1).getNewXYZ(w);
    inter.isRealNew=CircleEqns.calculateHypCC(t,u,v,w,x,(i==0));
    return x;
  }  
}

class HyperPTonLINE extends HyperPoint {
	public HyperPTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class HyperPTonCIRC extends HyperPoint {
	public HyperPTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}

class HyperLINEintLINE extends HyperPoint {
	public HyperLINEintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		double[] v1={0,0,0},v2={0,0,0},nm={0,0,0};
		if (constList.get(0).getValidNew() &&
	        constList.get(1).getValidNew()) {
	        constList.get(0).getNewXYZ(v1);
	        constList.get(1).getNewXYZ(v2);
	        setValidNew(MathEqns.hypLineIntLine(v1,v2,nm));
	        setNewXYZ(nm);
			for (int i=0;i<2;i++) {
				if (constList.get(i).getType()==SEGMENT) {
					constList.get(i).get(0).getNewXYZ(v1);
					constList.get(i).get(1).getNewXYZ(v2);
					if (Math.abs(MathEqns.acosh(MathEqns.hypProduct(v1,v2))-MathEqns.acosh(MathEqns.hypProduct(v1,nm))-MathEqns.acosh(MathEqns.hypProduct(nm,v2)))>.0001)
						setValidNew(false);
				}
				if (constList.get(i).getType()==RAY) {
					constList.get(i).get(0).getNewXYZ(v1);
					constList.get(i).get(1).getNewXYZ(v2);
					if (Math.abs(MathEqns.acosh(MathEqns.hypProduct(nm,v2))-MathEqns.acosh(MathEqns.hypProduct(v1,nm))-MathEqns.acosh(MathEqns.hypProduct(v1,v2)))<.0001)
						setValidNew(false);
				}
			}
	    }
	    else setValidNew(false);	
	}
}


class HyperCIRCintLINE extends HyperPoint {
	public HyperCIRCintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		setNewXYZ(((HyperCircle)constList.get(0)).getNewCLxyz(this,(HyperConstruct)constList.get(1),type-CIRCintLINE0));
		double[] nm={newX,newY,newZ};
		for (int i=0;i<2;i++) {
			if (constList.get(i).getType()==SEGMENT) {
				constList.get(i).get(0).getNewXYZ(v1);
				constList.get(i).get(1).getNewXYZ(v2);
				if (Math.abs(MathEqns.acosh(MathEqns.hypProduct(v1,v2))-MathEqns.acosh(MathEqns.hypProduct(v1,nm))-MathEqns.acosh(MathEqns.hypProduct(nm,v2)))>.00001)
					setValidNew(false);
			}
			if (constList.get(i).getType()==RAY) {
				constList.get(i).get(0).getNewXYZ(v1);
				constList.get(i).get(1).getNewXYZ(v2);
				if (Math.abs(MathEqns.acosh(MathEqns.hypProduct(nm,v2))-MathEqns.acosh(MathEqns.hypProduct(nm,v1))-MathEqns.acosh(MathEqns.hypProduct(v1,v2)))<.00001)
					setValidNew(false);
			}
		}
	}
}

class HyperCIRCintCIRC extends HyperPoint {
	public HyperCIRCintCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		//super.update();
		setNewXYZ(((HyperCircle)constList.get(0)).getNewCCxyz(this,(HyperConstruct)constList.get(1),type-CIRCintCIRC00));
	}
}

class HyperMIDPT extends HyperPoint {
	public HyperMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		//super.update();
		double[] v1={0,0,0},v2={0,0,0};
		LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		HyperPoint p1 = new HyperPoint(POINT,tempList,v1);
		HyperPoint p2 = new HyperPoint(POINT,tempList,v2);
		tempList.clear();	tempList.add(p1);	tempList.add(p2);
		HyperCircle a = new HyperCircle(CIRCLE,tempList);
		tempList.clear();	tempList.add(p2);	tempList.add(p1);
		HyperCircle b = new HyperCircle(CIRCLE,tempList);
		HyperPoint i1,i2,i3;
		i1=((HyperCircle)a).intersect(0,(HyperCircle)b);
		i1.update();	i1.getNewXYZ(v1);	i1.setXYZ(v1);
		i2=((HyperCircle)a).intersect(1,(HyperCircle)b);
		i2.update();	i2.getNewXYZ(v2);	i2.setXYZ(v2);
		tempList.clear();	tempList.add(p1);	tempList.add(p2);
		HyperLine c=new HyperLine(LINE,tempList);
		c.update();
		tempList.clear();
		tempList.add(i1);		tempList.add(i2);
		HyperLine d=new HyperLine(LINE,tempList);
		i3=((HyperLine)c).intersect(0,(HyperLine)d);
		i3.update(); i3.getNewXYZ(v1);
		setNewXYZ(v1);
	}
}

class HyperFIXedPT extends HyperPoint {
	public HyperFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}
class HyperReflectPt extends HyperPoint {
	public HyperReflectPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		tempList.add(constList.get(0).get(0));
		tempList.add(constList.get(0).get(1));
		HyperLine l;
		if (constList.get(0).getType()==PERP) l = new HyperPERP(PERP,tempList);
		else if (constList.get(0).getType()==PARLL0) l = new HyperPARLL0(PARLL0,tempList);
		else if (constList.get(0).getType()==PARLL1) l = new HyperPARLL1(PARLL1,tempList);
		else if (constList.get(0).getType()==BISECTOR) {
			tempList.add(constList.get(0).get(2));
			l = new HyperBISECTOR(BISECTOR,tempList);
		}
		else l = new HyperLine(constList.get(0).getType(),tempList);
		l.update();			l.getNewXYZ(v);		l.setXYZ(v);	
		constList.get(1).getNewXYZ(u);
		tempList.clear();
		HyperPoint p = new HyperPoint(POINT,tempList,u);
		// if the point lies on the line of reflection, then
		if (Math.abs(MathEqns.dotProduct(u,v))<.0000001) setNewXYZ(u);
		else { // otherwise
			tempList.clear();	tempList.add(l);	tempList.add(p);
			HyperPERP a=new HyperPERP(PERP,tempList);
			a.update();		a.getNewXYZ(w);		a.setXYZ(w);
			HyperPoint b,x0,x1;
			b=((HyperLine)a).intersect(0,(HyperLine)l);
			b.update();		b.getNewXYZ(w);		b.setXYZ(w);
			tempList.clear();	tempList.add(b);	tempList.add(p);
			HyperCircle c=new HyperCircle(CIRCLE,tempList);
			x0=((HyperLine)a).intersect(0,(HyperCircle)c);
			x0.update();	x0.getNewXYZ(v);	x0.setXYZ(v);
			x1=((HyperLine)a).intersect(1,(HyperCircle)c);
			x1.update();	x1.getNewXYZ(w);	x1.setXYZ(w);
			if (MathEqns.hypProduct(u,v)>MathEqns.hypProduct(u,w)) setNewXYZ(v);
			else setNewXYZ(w);
		}
	}
}
class HyperTranslatePt extends HyperPoint {
	public HyperTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		HyperPoint a,b,c;
		constList.get(0).get(0).getNewXYZ(w);	a=new HyperPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new HyperPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			c=new HyperPoint(POINT,tempList,w);
		tempList.add(a);	tempList.add(b);
		HyperLine d = new HyperLine(LINE,tempList);
		d.update();			d.getNewXYZ(w);		d.setXYZ(w);
		HyperMIDPT e = new HyperMIDPT(MIDPT,tempList,w);
		e.update();			e.getNewXYZ(w);		e.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(a);
		HyperPERP f = new HyperPERP(PERP,tempList);
		f.update();			f.getNewXYZ(w);		f.setXYZ(w);
		tempList.clear();	tempList.add(d);	tempList.add(e);
		HyperPERP g = new HyperPERP(PERP,tempList);
		g.update();			g.getNewXYZ(w);		g.setXYZ(w);
		tempList.clear();	tempList.add(f);	tempList.add(c);
		HyperReflectPt h = new HyperReflectPt(REFLECT_PT,tempList,w);
		h.update();			h.getNewXYZ(w);		h.setXYZ(w);
		tempList.clear();	tempList.add(g);	tempList.add(h);
		HyperReflectPt i = new HyperReflectPt(REFLECT_PT,tempList,w);
		i.update();			i.getNewXYZ(w);		setNewXYZ(w);
	}
}
class HyperInvertPt extends HyperPoint {
	public HyperInvertPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).get(0).getNewXYZ(u);
		constList.get(0).get(1).getNewXYZ(v);
		constList.get(1).getNewXYZ(w);
		if (MathEqns.norm(u,w)<.00001) setValidNew(false);
		else {
			setValidNew(true);
			HyperPoint a,b,c;
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			a=new HyperPoint(POINT,tempList,u);
			b=new HyperPoint(POINT,tempList,w);
			if (MathEqns.acosh(MathEqns.hypProduct(u,w))<.001) setValidNew(false);
			else {
			double cd=Math.cosh(MathEqns.acosh(MathEqns.hypProduct(u,v))
						        *MathEqns.acosh(MathEqns.hypProduct(u,v))
						        /MathEqns.acosh(MathEqns.hypProduct(u,w)));
			double k=(-u[1]*u[1]*u[0]+u[0]*u[2]*u[2]-u[0]*cd-u[0]*u[0]*u[0]+Math.sqrt(u[1]*u[1]*u[0]*u[0]*u[2]*u[2]-u[2]*u[2]*u[2]*u[2]*u[1]*u[1]+u[0]*u[0]*u[2]*u[2]-u[2]*u[2]*u[2]*u[2]+u[2]*u[2]*u[1]*u[1]*u[1]*u[1]+u[2]*u[2]*cd*cd+2*u[2]*u[2]*u[1]*u[1]*cd))/(u[0]*u[0]-u[2]*u[2]);
			u[0]+=k;u[2]=Math.sqrt(1+u[0]*u[0]+u[1]*u[1]);
			c=new HyperPoint(POINT,tempList,u);
			tempList.add(a);	tempList.add(b);
			HyperLine d = new HyperLine(LINE,tempList);
			d.update();			d.getNewXYZ(v);		d.setXYZ(v);
			tempList.clear();	tempList.add(a);	tempList.add(c);
			HyperCircle e = new HyperCircle(CIRCLE,tempList);
			HyperPoint f1,f2;
			f1=((HyperLine)d).intersect(0,(HyperCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((HyperLine)d).intersect(1,(HyperCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			if (Math.abs(MathEqns.hypProduct(v1,w))<Math.abs(MathEqns.hypProduct(v2,w))) setNewXYZ(v1);
			else setNewXYZ(v2);
			getNewXYZ(w);
			if (w[0]==1 && w[1]==0 && w[2]==0) setValidNew(false);
			}
		}
	}
}
class HyperRotatePt extends HyperPoint {
	public HyperRotatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		HyperPoint a,b,c,d;
		constList.get(0).get(0).getNewXYZ(w);	a=new HyperPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new HyperPoint(POINT,tempList,w);
		constList.get(0).get(2).getNewXYZ(w);	c=new HyperPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			d=new HyperPoint(POINT,tempList,w);
		tempList.add(b);	tempList.add(a);
		HyperLine l0 = new HyperLine(LINE,tempList);
		l0.update();		l0.getNewXYZ(w);	l0.setXYZ(w);
		tempList.clear();	tempList.add(a);	tempList.add(b);	tempList.add(c);
		HyperBISECTOR l1 = new HyperBISECTOR(BISECTOR,tempList);
		l1.update();		l1.getNewXYZ(w);	l1.setXYZ(w);
		tempList.clear();	tempList.add(l0);	tempList.add(d);
		HyperReflectPt p0 = new HyperReflectPt(REFLECT_PT,tempList,w);
		p0.update();		p0.getNewXYZ(w);	p0.setXYZ(w);
		tempList.clear();	tempList.add(l1);	tempList.add(p0);
		HyperReflectPt p1 = new HyperReflectPt(REFLECT_PT,tempList,w);
		p1.update();		p1.getNewXYZ(w);	setNewXYZ(w);
	}
}
class HyperMeasure extends HyperPoint {
    protected double[] a={0,0,0},b={0,0,0};
	public HyperMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
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
	  }
	}

}
class HyperCOMMENT extends HyperPoint {
	public HyperCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText,SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
}
class HyperCONSTANT extends HyperPoint {
	public HyperCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		sz=(int)MathEqns.max(4,SZ/40);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}

}
class HyperSUM extends HyperMeasure {
	public HyperSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
class HyperRATIO extends HyperMeasure {
	public HyperRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		update();
		if (!New) {
			if (get(1).measureValue!=0) {
				measureValue=get(0).measureValue/get(1).measureValue;
				g.drawString(displayText+"\u2248"+MathEqns.chop(measureValue,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
				isReal=true;
			}
			else isReal=false;
		}
		else {
			if (get(1).measureValueNew!=0) {
				measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
				g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
				isRealNew=true;
			}
			else isRealNew=false;
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
class HyperTRIANGLE extends HyperMeasure {
	public HyperTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		double[] c={0,0,0};
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		double aa,bb,cc;
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
		  measureValue=1;
		  g.drawString(displayText+"\u2248"+MathEqns.chop(Math.PI-aa-bb-cc,GeoPlayground.digits),
					   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
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
		    measureValueNew=Math.PI-aa-bb-cc;
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
		    aa=MathEqns.hypAngle(b,a,c)/180*Math.PI;
		    get(0).getXYZ(a);	get(1).getXYZ(b);	get(2).getXYZ(c);
		    bb=MathEqns.hypAngle(a,b,c)/180*Math.PI;
		    get(0).getXYZ(a);	get(1).getXYZ(b);	get(2).getXYZ(c);
		    cc=MathEqns.hypAngle(a,c,b)/180*Math.PI;
		    measureValue=Math.PI-aa-bb-cc;
		}
	}
}
class HyperDISTANCE extends HyperMeasure {
	public HyperDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				   +MathEqns.chop(MathEqns.acosh(MathEqns.hypProduct(a,b)),GeoPlayground.digits),
				   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=MathEqns.acosh(MathEqns.hypProduct(a,b));
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=MathEqns.acosh(MathEqns.hypProduct(a,b));
		}
	}
}

class HyperANGLE extends HyperMeasure {
	public HyperANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		double[] c={0,0,0};
		  if (New) {
		    get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		  }
		  else {
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
		  }
		  measureValue=1;
		  if (GeoPlayground.degrees)
			  g.drawString(displayText+"\u2248"
						   +MathEqns.chop(MathEqns.hypAngle(a,b,c),GeoPlayground.digits)+"\u00b0",
						   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		  else
			  g.drawString(displayText+"\u2248"
					   +MathEqns.chop(MathEqns.hypAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits),
					   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0};
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			measureValueNew=MathEqns.hypAngle(a,b,c);
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			measureValue=MathEqns.hypAngle(a,b,c);
			if (!GeoPlayground.degrees) {
				measureValueNew=measureValueNew/180.*Math.PI;
				measureValue=measureValue/180.*Math.PI;
			}
		}
	}
}

class HyperCIRCUMF extends HyperMeasure {
	public HyperCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				   +MathEqns.chop(2*Math.PI*Math.sinh(MathEqns.acosh(MathEqns.hypProduct(a,b))),GeoPlayground.digits),
				   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=2*Math.sinh(MathEqns.acosh(MathEqns.hypProduct(a,b)))*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=2*Math.sinh(MathEqns.acosh(MathEqns.hypProduct(a,b)))*Math.PI;
		}
	}
}

class HyperAREA extends HyperMeasure {
	public HyperAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				   +MathEqns.chop(2*Math.PI*(Math.cosh(MathEqns.acosh(MathEqns.hypProduct(a,b)))-1),GeoPlayground.digits),
				   SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=(2*Math.PI*(Math.cosh(MathEqns.acosh(MathEqns.hypProduct(a,b)))-1));
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=(2*Math.PI*(Math.cosh(MathEqns.acosh(MathEqns.hypProduct(a,b)))-1));
		}
	}
}

class HyperPERP extends HyperLine {
	public HyperPERP(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] v0={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v0);
		MathEqns.hypPerp(v1,v0,v2);
		setNewXYZ(v2);
	}
}
class HyperPARLL0 extends HyperLine {
	public HyperPARLL0(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] v0={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v0);
		setValidNew(MathEqns.hypParallel(v1,v0,v2,false));
		setNewXYZ(v2);
	}
}
class HyperPARLL1 extends HyperLine {
	public HyperPARLL1(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] v0={0,0,0};
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v0);
		setValidNew(MathEqns.hypParallel(v1,v0,v2,true));
		setNewXYZ(v2);
	}
}
class HyperSEGMENT extends HyperLine {
	public HyperSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
	}
}

class HyperBISECTOR extends HyperLine {

	public HyperBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		double[] aa={0,0,0},bb={0,0,0},cc={0,0,0},mp={0,0,0};
		constList.get(0).getNewXYZ(aa);
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		HyperPoint a=new HyperPoint(POINT,tempList,aa);
		constList.get(1).getNewXYZ(bb);
		HyperPoint b=new HyperPoint(POINT,tempList,bb);
		constList.get(2).getNewXYZ(cc);
		HyperPoint c=new HyperPoint(POINT,tempList,cc);
		tempList.add(a);	tempList.add(c);
		HyperPoint m=new HyperMIDPT(MIDPT,tempList,mp);
		m.update();	m.getNewXYZ(mp);	m.setXYZ(mp);
		if (MathEqns.norm(mp,bb)<.001) {
			tempList.clear();	tempList.add(a);	tempList.add(c);
			HyperCircle d=new HyperCircle(CIRCLE,tempList);
			tempList.clear();	tempList.add(c);	tempList.add(a);
			HyperCircle e=new HyperCircle(CIRCLE,tempList);
			HyperPoint f1,f2;
			f1=((HyperCircle)d).intersect(0,(HyperCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((HyperCircle)d).intersect(1,(HyperCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			tempList.clear();	tempList.add(f1);	tempList.add(f2);
		}
		else {
			tempList.clear();	tempList.add(b);	tempList.add(c);
			HyperLine d=new HyperLine(LINE,tempList);
			d.update();	d.getNewXYZ(v1);
			tempList.clear();	tempList.add(b);	tempList.add(a);
			HyperCircle e=new HyperCircle(CIRCLE,tempList);
			HyperPoint f1,f2;
			f1=((HyperLine)d).intersect(0,(HyperCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((HyperLine)d).intersect(1,(HyperCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(f1);
			HyperPoint g1=new HyperMIDPT(MIDPT,tempList,v1);
			g1.update();	g1.getNewXYZ(v1);	g1.setXYZ(v1);
			tempList.clear();	tempList.add(a);	tempList.add(f2);
			HyperPoint g2=new HyperMIDPT(MIDPT,tempList,v2);
			g2.update();	g2.getNewXYZ(v2);	g2.setXYZ(v2);
			tempList.clear();	tempList.add(a);	tempList.add(c);
			HyperPoint g3=new HyperMIDPT(MIDPT,tempList,bb);
			g3.update();	g3.getNewXYZ(bb);	g3.setXYZ(bb);
			if (Math.abs(MathEqns.hypProduct(v1,bb))>Math.abs(MathEqns.hypProduct(v2,bb))) {
				g1=g2;
			}
			tempList.clear();	tempList.add(b);	tempList.add(g1);
		}
		HyperLine h=new HyperLine(LINE,tempList);
		h.update();	h.getNewXYZ(v1);
		setNewXYZ(v1);
	}
}
