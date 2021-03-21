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

abstract class ManhattanConstruct extends GeoConstruct {
	protected double[] v1={0,0,0},v2={0,0,0};//temp vectors

	public ManhattanConstruct(int t, LinkedList<GeoConstruct> clickedList) {
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
	public ManhattanConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v){
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
			get(0).getXYZ(v);
			get(1).getXYZ(v0);
			get(2).getXYZ(v1);
			for (int i=0;i<2;i++) v[i]=(v[i]+v0[i]+v1[i])/3;
		}
		if (t==CONSTANT) {
			measureValue=v[2];
			measureValueNew=v[2];
			v[2]=0;
		}
		setXYZ(v);
		update();
	}

	public abstract ManhattanPoint intersect(int m, ManhattanConstruct a);

	public void setXYZ(double[] vector) {
		x=vector[0]; y=vector[1]; z=0; 
		newX=x;      newY=y;      newZ=z;
	}

	public void setXYZ(double[] v1, double[] v2) {
		if (MathEqns.norm(v1,v2)>.0001) {
			if(type==LINE || type==SEGMENT || type==RAY){
				x=v2[0]-v1[0];y=v2[1]-v1[1];z=0;
			}
			else{
				x=-v1[1];y=v1[0];z=0;
			}
			setValid(true);
		}
		else setValid(false);
	}

	public void setNewXYZ(double[] vector) {
		if ((type<0 && MathEqns.norm(vector)>.0000001) || type>=0) {
		newX=vector[0]; newY=vector[1]; newZ=vector[2];}
		else setValidNew(false);
	}

	public void setNewXYZ(double[] v1, double[] v2) {
		if (MathEqns.norm(v1,v2)>.00001) {
			if(type==LINE || type==SEGMENT || type==RAY){
				newX=v2[0]-v1[0];newY=v2[1]-v1[1];newZ=0;
			}
			else{
				newX=-v1[1];newY=v1[0];newZ=0;
			}
			setValidNew(true);
		}
		else setValidNew(false);
	}

	public void translate(double[] dragStart, double[] dragNow){
		double[] vector1={0,0,0};
		getXYZ(vector1);
		for(int i=0;i<3;i++) vector1[i]+=dragNow[i]-dragStart[i];
		this.setNewXYZ(vector1);
	}
	public void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow) {
		MathEqns.transform(fixedObject,this,dragStart,dragNow);
	}
	public static double getScale() {return 1;}
	public static void setScale(double x) {}
} // end class 


class ManhattanPoint extends ManhattanConstruct{
	protected int sz;
	public ManhattanPoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}

	public ManhattanPoint intersect(int m, ManhattanConstruct a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(a);	tempList.add(this);
		ManhattanPoint newPoint=new ManhattanPoint(LINEintLINE0,tempList,v1);
		return newPoint;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		sz=(int)MathEqns.max(4,SZ/40);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    if ((New && getValidNew()) || (!New && getValid())) { 
			if(New) getNewXYZ(v1);
			else getXYZ(v1);
			if (getType()<30) {
				g.fillOval(SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2,sz,sz);
				if (getLabelShown())
					g.drawString(displayText,SZ+(int)(SZ*v1[0])-sz/2+fudge,SZ+(int)(SZ*v1[1])-sz/2); 
			}
		}
	}

	public boolean mouseIsOver(double[] v1, int SZ){
		double sz=MathEqns.max(6,SZ/20);
		return ((x-v1[0])*(x-v1[0])+(y-v1[1])*(y-v1[1])<sz*sz/(SZ*SZ));
	}
	
	
	protected void updatePTonLINE(){
		setValidNew(get(0).getValidNew());
		if (getValidNew()) {
			if (constList.get(0).getType()!=SEGMENT) {
				double[] newPt={0,0,0},lineSlope={0,0,0};
				double comp=0;
			constList.get(0).getNewXYZ(lineSlope);
			constList.get(0).get(1).getNewXYZ(v2);//This should be the "for sure" point
			v1[0]=v1[0]-v2[0];
			v1[1]=v1[1]-v2[1];
			MathEqns.normalize(lineSlope);
			comp=MathEqns.dotProduct(lineSlope, v1);
			newPt[0]=v2[0]+comp*lineSlope[0];
			newPt[1]=v2[1]+comp*lineSlope[1];
			newPt[2]=0;
			if (constList.get(0).getType()==RAY) {
				constList.get(0).get(0).getNewXYZ(v1);
				if (MathEqns.norm(newPt,v1)+MathEqns.norm(v1,v2)-MathEqns.norm(newPt,v2)<.000001)
					constList.get(0).get(0).getNewXYZ(newPt);
			}
			setNewXYZ(newPt);
			}
			else {
				double dist=1000;
				int min=0;
				double[] bn={0,0,0},nm={0,0,0};
				constList.get(0).get(0).getNewXYZ(v2);
				constList.get(0).get(1).getNewXYZ(bn);
				for (int i=0;i<127;i++) {
					nm[0]=v2[0]*i/126+bn[0]*(126-i)/126;
					nm[1]=v2[1]*i/126+bn[1]*(126-i)/126;
					if (MathEqns.norm(v1,nm)<dist) {
						dist=MathEqns.norm(v1,nm);
						min=i;
					}
				}
				nm[0]=v2[0]*min/126+bn[0]*(126-min)/126;
				nm[1]=v2[1]*min/126+bn[1]*(126-min)/126;
				nm[2]=0;
				if (constList.get(0).getType()==RAY)
					if (MathEqns.norm(nm,v2)+MathEqns.norm(nm,bn)-MathEqns.norm(nm,bn)<.000001)
						constList.get(0).get(0).getNewXYZ(nm);
				setNewXYZ(nm);
			}
		}
	}

	protected void updatePTonCIRC(){
		setValidNew(get(0).getValidNew());
		if (getValidNew()) {
			double[] center={0,0,0}, radial={0,0,0}, u1={0,0,0},u2={0,0,0};
			constList.get(0).get(0).getNewXYZ(center);
			constList.get(0).get(1).getNewXYZ(radial);
			double r=Math.abs(center[0]-radial[0])+Math.abs(center[1]-radial[1]);
			if (center[0]<=v1[0] && center[1]<=v1[1]) {
				u1[0]=center[0]+r;	u1[1]=center[1];
				u2[0]=center[0];	u2[1]=center[1]+r;
			}
			else if (center[0]<=v1[0] && center[1]>v1[1]) {
				u1[0]=center[0]+r;	u1[1]=center[1];
				u2[0]=center[0];	u2[1]=center[1]-r;
			}
			else if (center[0]>v1[0] && center[1]<=v1[1]) {
				u1[0]=center[0]-r;	u1[1]=center[1];
				u2[0]=center[0];	u2[1]=center[1]+r;
			}
			else {
				u1[0]=center[0]-r;	u1[1]=center[1];
				u2[0]=center[0];	u2[1]=center[1]-r;
			}
			ManhattanPoint a,b,d;
			ManhattanSEGMENT c;
			LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
			a=new ManhattanPoint(POINT,temp,u1);
			b=new ManhattanPoint(POINT,temp,u2);
			temp.add(a);	temp.add(b);
			c=new ManhattanSEGMENT(SEGMENT,temp);
			c.update();		temp.clear();	temp.add(c);
			d=new ManhattanPTonLINE(PTonLINE,temp,v1);
			d.updatePTonLINE();
			d.getNewXYZ(v2);	setNewXYZ(v2);
		}	
	}

	public void update() {
		boolean nowValid=true;
		for (int i=0;i<constList.size();i++) nowValid = (nowValid && constList.get(i).getValidNew());
		setValidNew(nowValid);
		if (type!=RATIO && type!=SUM && type!=DIFF && type!=PROD) getNewXYZ(v1);
	}
}

class ManhattanLine extends ManhattanConstruct{
	protected double[] a={0,0,0},u={0,0,0},v={0,0,0};
	public ManhattanLine(int t, LinkedList<GeoConstruct> clickedList){
		super(t,clickedList);
			x=clickedList.get(0).x-clickedList.get(1).x;
			y=clickedList.get(0).y-clickedList.get(1).y;
			z=0;
			newX=x;	newY=y;	newZ=0;
	}
	public ManhattanPoint intersect(int m, ManhattanConstruct a){
		ManhattanPoint newPoint;
		if(a.getType()==0)
			newPoint=intersect(m,(ManhattanCircle)a);
		else
			newPoint=intersect(m,(ManhattanLine)a);
		return newPoint;
	}
	public ManhattanPoint intersect(int m, ManhattanLine a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(a);	tempList.add(this);
		ManhattanPoint newPoint=new ManhattanLINEintLINE(LINEintLINE0,tempList,v);
		newPoint.update();
		newPoint.getNewXYZ(v);
		newPoint.setXYZ(v);
		return newPoint;
	}
	public ManhattanPoint intersect(int m, ManhattanCircle a){
		ManhattanPoint newPoint=a.intersect(m,this);
		return newPoint;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		    if(New){
				constList.get(0).getNewXYZ(v1);
				constList.get(1).getNewXYZ(v2);
			}
			else{
				constList.get(0).getXYZ(v1);
				constList.get(1).getXYZ(v2);
			}
			if (MathEqns.norm(v1,v2)>.00001) {
				if(type==LINE || type==SEGMENT || type==RAY){
					g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),
							SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]));
					if (getLabelShown()) g.drawString(displayText,
							SZ+(int)(SZ*(2*v2[0]+v1[0])/3)+fudge,SZ+(int)(SZ*(2*v2[1]+v1[1])/3));
					if (type==RAY) {
						double nrm=MathEqns.norm(v2,v1);
						if (nrm>.00001){
							double[] vector={(v2[0]-v1[0])/nrm,(v2[1]-v1[1])/nrm};
							g.drawLine(SZ+(int)(SZ*v2[0])+fudge,SZ+(int)(SZ*v2[1]),
									SZ+(int)(SZ*v2[0]+Math.tan(344./219)*vector[0])+fudge,SZ+(int)(SZ*v2[0]+Math.tan(344./219)*vector[1]));
						}
					}
					if (type==LINE) {//extend the line
						if (Math.abs(v1[0]-v2[0])>.0001) {
						double tmp[]={0,0,0};
						if(v1[0]<v2[0]){
							tmp[0]=v1[0];
							tmp[1]=v1[1];
						}
						else{
							tmp[0]=v2[0];
							tmp[1]=v2[1];						
						}
						double m=(v2[1]-v1[1])/(v2[0]-v1[0]);
						for(double t=-Math.PI/2+.1;t<Math.PI/2-.1;t+=.1){
							x=Math.tan(t)+tmp[0];
							y=m*x+tmp[1]-m*tmp[0];
							g.drawLine(SZ+(int)(SZ*tmp[0])+fudge,SZ+(int)(SZ*tmp[1]),
							SZ+(int)(SZ*x)+fudge,SZ+(int)(SZ*y));
							tmp[0]=x;
							tmp[1]=y;
							if(t<0&&(t+.1>0)){
								if(v1[0]>v2[0]){
									tmp[0]=v1[0];
									tmp[1]=v1[1];
								}
								else{
									tmp[0]=v2[0];
									tmp[1]=v2[1];
								}
							}
						}
						}
						else {
							g.drawLine(SZ+(int)(SZ*v1[0])+fudge,0,SZ+(int)(SZ*v1[0])+fudge,2*SZ);
						}
					}
				}
			}
		}
	}

	public boolean mouseIsOver(double[] v0, int SZ){
		double[] v2={0,0,0},v={0,0,0},r={0,0,0};
		constList.get(1).getXYZ(v2);
		v[0]=-y;
		v[1]=x;
		MathEqns.normalize(v);

		r[0]=v2[0]-v0[0];
		r[1]=v2[1]-v0[1];
		//Return true if the distance between the line and the point in question is less than
		return Math.abs(MathEqns.dotProduct(v,r))< 0.03;
	}
	public void update() {
		boolean isValidNow=true;
		for (int i=0;i<getSize();i++)
			isValidNow=(isValidNow && get(i).getValidNew());
		setValidNew(isValidNow);
		if (isValidNow) {
			constList.get(0).getNewXYZ(v1);
			constList.get(1).getNewXYZ(v2);			
			if (type==LINE || type==SEGMENT || type==RAY){
				if (MathEqns.norm(v1,v2)>.00001) setNewXYZ(v1,v2);
				else setValidNew(false);
			}
		}
	}
}

class ManhattanCircle extends ManhattanConstruct{
	public ManhattanCircle(int t, LinkedList<GeoConstruct> clickedList) {
		super(t,clickedList);
		if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
	}
	public ManhattanPoint intersect(int m, ManhattanConstruct a){
		ManhattanPoint newPoint;
		if(a.getType()==0)
			newPoint=intersect(m,(ManhattanCircle)a);
		else
			newPoint=intersect(m,(ManhattanLine)a);
		return newPoint;
	}
	public ManhattanPoint intersect(int m, ManhattanLine a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(this);	tempList.add(a);
		ManhattanPoint newPoint=new ManhattanCIRCintLINE(CIRCintLINE0+m,tempList,v1);
		newPoint.setXYZ(getCLxyz(newPoint,a,m));
		return newPoint;
	}
	public ManhattanPoint intersect(int m, ManhattanCircle a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(this);	tempList.add(a);
		ManhattanPoint newPoint=new ManhattanCIRCintCIRC(CIRCintCIRC00+m,tempList,v1);
		newPoint.setXYZ(getCCxyz(newPoint,a,m));
		return newPoint;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			double[] center={0,0,0},radial={0,0,0},v1={0,0,0},v2={0,0,0};
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if(New){
				constList.get(0).getNewXYZ(center);
				constList.get(1).getNewXYZ(radial);
			}
			else{
				constList.get(0).getXYZ(center);
				constList.get(1).getXYZ(radial);
			}
			double r=Math.abs(center[0]-radial[0])+Math.abs(center[1]-radial[1]);
			EuclideanSEGMENT c;
			v1[0]=center[0]+r;		v1[1]=center[1];
			v2[0]=center[0];		v2[1]=center[1]+r;
			c=getSegment(v1,v2);	c.draw(g, SZ, New);
			v1[0]=center[0]-r;
			c=getSegment(v1,v2);	c.draw(g, SZ, New);
			v2[1]=center[1]-r;
			c=getSegment(v1,v2);	c.draw(g, SZ, New);
			v1[0]=center[0]+r;
			c=getSegment(v1,v2);	c.draw(g, SZ, New);			
			if (getLabelShown())
				g.drawString(displayText,SZ+(int)(SZ*(center[0]+r/2))+fudge,SZ+(int)(SZ*(center[1]+r/2)));	  
		}
	}
	public EuclideanSEGMENT getSegment(double[] v1, double[] v2){		// we use Euclidean segments
		LinkedList<GeoConstruct> temp=new LinkedList<GeoConstruct>();	// to draw Manhattan circles
		EuclideanPoint a=new EuclideanPoint(POINT,temp,v1);
		EuclideanPoint b=new EuclideanPoint(POINT,temp,v2);
		temp.add(a);	temp.add(b);
		EuclideanSEGMENT c=new EuclideanSEGMENT(SEGMENT,temp);
		c.update();
		return c;
	}
	public boolean mouseIsOver(double[] mouse, int SZ){
		double[] axis={0,0,0}, point={0,0,0};
		double radius,dFromAxis;
		constList.get(0).getXYZ(axis);
		constList.get(1).getXYZ(point);
		radius=Math.abs(axis[0]-point[0])+Math.abs(axis[1]-point[1]);
		dFromAxis=Math.abs(axis[0]-mouse[0])+Math.abs(axis[1]-mouse[1]);
		return Math.abs(radius-dFromAxis)<0.03;
	}

	public void update() {
		setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
	}
	public double[] getCLxyz(ManhattanPoint inter, ManhattanConstruct b, int i) {
		double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
		get(0).getXYZ(u); 
		get(1).getXYZ(v);
		b.getXYZ(w);
		b.get(1).getXYZ(z);
		inter.setValid(CircleEqns.calculateManCL(u,v,w,z,x,(i==0)));
		return x;
	}

	public double[] getNewCLxyz(ManhattanPoint inter,ManhattanConstruct b, int i) {
		double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
		get(0).getNewXYZ(u);
		get(1).getNewXYZ(v);
		b.getNewXYZ(w);
		b.get(1).getNewXYZ(z);
		inter.setValidNew(CircleEqns.calculateManCL(u,v,w,z,x,(i==0)));
		return x;
	}
	public double[] getCCxyz(ManhattanPoint inter,ManhattanConstruct b, int i) {
		double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
		get(0).getXYZ(t);
		get(1).getXYZ(u);
		b.get(0).getXYZ(v);
		b.get(1).getXYZ(w);
		if (t[0]==v[0]) v[0]+=.00000001;
		inter.setValid(CircleEqns.calculateManCC(t,u,v,w,x,(i==0)));
		return x;
	}
	public double[] getNewCCxyz(ManhattanPoint inter,ManhattanConstruct b, int i) {
		double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
		get(0).getNewXYZ(t);
		get(1).getNewXYZ(u);
		b.get(0).getNewXYZ(v);
		b.get(1).getNewXYZ(w);
		if (t[0]==v[0]) v[0]+=.00000001;
		inter.setValidNew(CircleEqns.calculateManCC(t,u,v,w,x,(i==0)));
		return x;
	}

}

class ManhattanPTonLINE extends ManhattanPoint {
	public ManhattanPTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class ManhattanPTonCIRC extends ManhattanPoint {
	public ManhattanPTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}

class ManhattanLINEintLINE extends ManhattanPoint {
	public ManhattanLINEintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		double[] v1={0,0,0},v2={0,0,0},nm={0,0,0};
		get(0).getNewXYZ(v1);
		get(1).getNewXYZ(v2);
		nm[2]=v2[0]*v1[1]-v2[1]*v1[0];
		double[] point1={0,0,0},point2={0,0,0};
		get(0).get(1).getNewXYZ(point1);
		get(1).get(1).getNewXYZ(point2);

		double s=((point2[1]-point1[1])*v1[0] - (point2[0]-point1[0])*v1[1])/(v2[0]*v1[1]-v2[1]*v1[0]);
		if (MathEqns.dotProduct(nm,nm)>0) {
			nm[0]=v2[0]*s+point2[0];
			nm[1]=v2[1]*s+point2[1];
			nm[2]=0;
			setValidNew(true);
			setNewXYZ(nm);
			for (int i=0;i<2;i++) {
				if (constList.get(i).getType()==SEGMENT) {
					constList.get(i).get(0).getNewXYZ(v1);
					constList.get(i).get(1).getNewXYZ(v2);
					if (Math.abs(Math.abs(v1[0]-v2[0])+Math.abs(v1[1]-v2[1])-
							 Math.abs(v1[0]-nm[0])-Math.abs(v1[1]-nm[1])-
							 Math.abs(nm[0]-v2[0])-Math.abs(nm[1]-v2[1]))>.001)
						setValidNew(false);
				}
				if (constList.get(i).getType()==RAY) {
					constList.get(i).get(0).getNewXYZ(v1);
					constList.get(i).get(1).getNewXYZ(v2);
					if (Math.abs(MathEqns.norm(nm,v1)+MathEqns.norm(v1,v2)-MathEqns.norm(nm,v2))<.00001)
						setValidNew(false);
				}
			}
		}
		else setValid(false);
	}
}

class ManhattanCIRCintLINE extends ManhattanPoint {
	public ManhattanCIRCintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		setNewXYZ(((ManhattanCircle)constList.get(0)).getNewCLxyz(this,(ManhattanConstruct)constList.get(1),type-CIRCintLINE0));
		double[] nm={newX,newY,newZ};
		for (int i=0;i<2;i++) {
			if (constList.get(i).getType()==SEGMENT) {
				constList.get(i).get(0).getNewXYZ(v1);
				constList.get(i).get(1).getNewXYZ(v2);
				if (Math.abs(MathEqns.norm(v1,nm)+MathEqns.norm(nm,v2)-MathEqns.norm(v1,v2))>.00001)
				setValidNew(false);
			}
			if (constList.get(i).getType()==RAY) {
				constList.get(i).get(0).getNewXYZ(v1);
				constList.get(i).get(1).getNewXYZ(v2);
				if (Math.abs(MathEqns.norm(nm,v1)+MathEqns.norm(v1,v2)-MathEqns.norm(nm,v2))<.00001)
				setValidNew(false);
			}
		}
	}
}

class ManhattanCIRCintCIRC extends ManhattanPoint {
	public ManhattanCIRCintCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		setNewXYZ(((ManhattanCircle)constList.get(0)).getNewCCxyz(this,(ManhattanConstruct)constList.get(1),type-CIRCintCIRC00));
	}
}

class ManhattanMIDPT extends ManhattanPoint {
	public ManhattanMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		v1[0]=(v1[0]+v2[0])/2;
		v1[1]=(v1[1]+v2[1])/2;
		setNewXYZ(v1);
	}
}

class ManhattanFIXedPT extends ManhattanPoint {
	public ManhattanFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
}
class ManhattanInvertPt extends ManhattanPoint {
	public ManhattanInvertPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).get(0).getNewXYZ(u);
		constList.get(0).get(1).getNewXYZ(v);
		constList.get(1).getNewXYZ(w);
		if (MathEqns.norm(u,w)<.0000001) setValidNew(false);
		else {
			setValidNew(true);
			ManhattanPoint a,b,c;
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			a=new ManhattanPoint(POINT,tempList,u);
			b=new ManhattanPoint(POINT,tempList,w);
			u[0]+=(Math.abs(u[0]-v[0])+Math.abs(u[1]-v[1]))*(Math.abs(u[0]-v[0])+Math.abs(u[1]-v[1]))/(Math.abs(u[0]-w[0])+Math.abs(u[1]-w[1]));
			c=new ManhattanPoint(POINT,tempList,u);
			tempList.add(a);	tempList.add(b);
			ManhattanLine d = new ManhattanLine(LINE,tempList);
			d.update();			d.getNewXYZ(v);		d.setXYZ(v);
			tempList.clear();	tempList.add(a);	tempList.add(c);
			ManhattanCircle e = new ManhattanCircle(CIRCLE,tempList);
			ManhattanPoint f1,f2;
			f1=((ManhattanLine)d).intersect(0,(ManhattanCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((ManhattanLine)d).intersect(1,(ManhattanCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			if (MathEqns.norm(v1,w)<MathEqns.norm(v2,w)) setNewXYZ(v1);
			else setNewXYZ(v2);
		}
	}
}
class ManhattanTranslatePt extends ManhattanPoint {
	public ManhattanTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		EuclideanPoint a,b,c;
		constList.get(0).get(0).getNewXYZ(w);	a=new EuclideanPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new EuclideanPoint(POINT,tempList,w);
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
		i.update();			i.getNewXYZ(w);		setNewXYZ(w);
	}
}
class ManhattanReflectPt extends ManhattanPoint {
	public ManhattanReflectPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		tempList.add(constList.get(0).get(0));
		tempList.add(constList.get(0).get(1));
		ManhattanLine l;
		if (constList.get(0).getType()==BISECTOR) {
			tempList.add(constList.get(0).get(2));
			l = new ManhattanBISECTOR(BISECTOR,tempList);
		}
		else if (constList.get(0).getType()==PERP) l = new ManhattanPERP(PERP,tempList);
		else if (constList.get(0).getType()==PARLL0) l = new ManhattanPARLL0(PARLL0,tempList);
		else l = new ManhattanLine(constList.get(0).getType(),tempList);
		l.update();			l.getNewXYZ(w);		l.setXYZ(w);	
		constList.get(1).getNewXYZ(w);			tempList.clear();
		ManhattanPoint p = new ManhattanPoint(POINT,tempList,w);
		tempList.clear();	tempList.add(l);	tempList.add(p);
		ManhattanPERP a=new ManhattanPERP(PERP,tempList);
		a.update();		a.getNewXYZ(w);		a.setXYZ(w);
		ManhattanPoint b,x0,x1;
		b=((ManhattanLine)a).intersect(0,(ManhattanLine)l);
		b.update();		b.getNewXYZ(w);		b.setXYZ(w);
		tempList.clear();	tempList.add(b);	tempList.add(p);
		ManhattanCircle c=new ManhattanCircle(CIRCLE,tempList);
		x0=((ManhattanLine)a).intersect(0,(ManhattanCircle)c);
		x0.update();	x0.getNewXYZ(v);	x0.setXYZ(v);
		x1=((ManhattanLine)a).intersect(1,(ManhattanCircle)c);
		x1.update();	x1.getNewXYZ(w);	x1.setXYZ(w);
		p.getNewXYZ(u);
		if (MathEqns.norm(u,v)>MathEqns.norm(u,w)) setNewXYZ(v);
		else setNewXYZ(w);
	}
}
class ManhattanRotatePt extends ManhattanPoint {
	public ManhattanRotatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		ManhattanPoint a,b,c,d;
		constList.get(0).get(0).getNewXYZ(w);	a=new ManhattanPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new ManhattanPoint(POINT,tempList,w);
		constList.get(0).get(2).getNewXYZ(w);	c=new ManhattanPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			d=new ManhattanPoint(POINT,tempList,w);
		tempList.add(b);	tempList.add(a);
		ManhattanLine l0 = new ManhattanLine(LINE,tempList);
		l0.update();		l0.getNewXYZ(w);	l0.setXYZ(w);
		tempList.clear();	tempList.add(a);	tempList.add(b);	tempList.add(c);
		ManhattanBISECTOR l1 = new ManhattanBISECTOR(BISECTOR,tempList);
		l1.update();		l1.getNewXYZ(w);	l1.setXYZ(w);
		tempList.clear();	tempList.add(l0);	tempList.add(d);
		ManhattanReflectPt p0 = new ManhattanReflectPt(REFLECT_PT,tempList,w);
		p0.update();		p0.getNewXYZ(w);	p0.setXYZ(w);
		tempList.clear();	tempList.add(l1);	tempList.add(p0);
		ManhattanReflectPt p1 = new ManhattanReflectPt(REFLECT_PT,tempList,w);
		p1.update();		p1.getNewXYZ(w);	p1.setXYZ(w);
		b.getNewXYZ(u);		d.getNewXYZ(v);
		for (int i=0;i<3;i++) w[i]-=u[i];
		double n0=Math.abs(u[0]-v[0])+Math.abs(u[1]-v[1])+Math.abs(u[2]-v[2]);
		double n1=Math.abs(w[0])+Math.abs(w[1])+Math.abs(w[2]);
		if (n0>0.000001 && n1>0.000001) {
			for (int i=0;i<3;i++) w[i]=w[i]/n1*n0;
			for (int i=0;i<3;i++) w[i]+=u[i];
			setNewXYZ(w);
		}
		else setNewXYZ(v);
	}
}
class ManhattanMeasure extends ManhattanPoint {
    protected double[] a={0,0,0},b={0,0,0};
	public ManhattanMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
		super(t, clickedList, v);
		setDisplayText();
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	  if ((New && getValidNew()) || (!New && getValid())) {
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
		}
		else  {
			get(0).getXYZ(a);		get(1).getXYZ(b);
		}
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
	  }
	}

}
class ManhattanCOMMENT extends ManhattanPoint {
	public ManhattanCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText,SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
}
class ManhattanCONSTANT extends ManhattanPoint {
	public ManhattanCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}

}
class ManhattanSUM extends ManhattanMeasure {
	public ManhattanSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
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
class ManhattanRATIO extends ManhattanMeasure {
	public ManhattanRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			super.draw(g, SZ, New);
			update();
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
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
class ManhattanDISTANCE extends ManhattanMeasure {
	public ManhattanDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    g.drawString(displayText+"\u2248"
				+MathEqns.chop(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]),GeoPlayground.digits),
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]);
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]);
		}
	}
}

class ManhattanTRIANGLE extends ManhattanMeasure {
	public ManhattanTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		double[] c={0,0,0},d={0,0,0};
		if (New) get(2).getNewXYZ(c);
		else get(2).getXYZ(c);
		if (a[0]>b[0]) for (int i=0;i<3;i++) {d[i]=a[i];a[i]=b[i];b[i]=d[i];}
		if (b[0]>c[0]) for (int i=0;i<3;i++) {d[i]=b[i];b[i]=c[i];c[i]=d[i];}
		if (a[0]>b[0]) for (int i=0;i<3;i++) {d[i]=a[i];a[i]=b[i];b[i]=d[i];}
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    g.drawString(displayText+"\u2248"
				+MathEqns.chop(Math.abs((c[1]*a[0]-b[1]*a[0]+a[1]*b[0]-a[1]*c[0]+b[1]*c[0]-c[1]*b[0])/2),GeoPlayground.digits),
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		double[] c={0,0,0},d={0,0,0};
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			if (a[0]>b[0]) for (int i=0;i<3;i++) {d[i]=a[i];a[i]=b[i];b[i]=d[i];}
			if (b[0]>c[0]) for (int i=0;i<3;i++) {d[i]=b[i];b[i]=c[i];c[i]=d[i];}
			if (a[0]>b[0]) for (int i=0;i<3;i++) {d[i]=a[i];a[i]=b[i];b[i]=d[i];}
			measureValueNew=Math.abs((c[1]*a[0]-b[1]*a[0]+a[1]*b[0]-a[1]*c[0]+b[1]*c[0]-c[1]*b[0])/2);
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getNewXYZ(c);
			if (a[0]>b[0]) for (int i=0;i<3;i++) {d[i]=a[i];a[i]=b[i];b[i]=d[i];}
			if (b[0]>c[0]) for (int i=0;i<3;i++) {d[i]=b[i];b[i]=c[i];c[i]=d[i];}
			if (a[0]>b[0]) for (int i=0;i<3;i++) {d[i]=a[i];a[i]=b[i];b[i]=d[i];}
			measureValue=Math.abs((c[1]*a[0]-b[1]*a[0]+a[1]*b[0]-a[1]*c[0]+b[1]*c[0]-c[1]*b[0])/2);
		}
	}
}
class ManhattanANGLE extends ManhattanMeasure {
	public ManhattanANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		double[] c={0,0,0};
		if (New) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
		}
		else {
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
		}
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    if (GeoPlayground.degrees)
			g.drawString(displayText+"\u2248"
				+MathEqns.chop(MathEqns.eucAngle(a,b,c),GeoPlayground.digits)+"\u00b0",
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		else
			g.drawString(displayText+"\u2248"
					+MathEqns.chop(MathEqns.eucAngle(a,b,c)*Math.PI/180.,GeoPlayground.digits),
					SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			double[] c={0,0,0};
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);	get(2).getNewXYZ(c);
			measureValueNew=MathEqns.eucAngle(a,b,c);
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			measureValue=MathEqns.eucAngle(a,b,c);
			if (!GeoPlayground.degrees) {
				measureValueNew=measureValueNew/180.*Math.PI;
				measureValue=measureValue/180.*Math.PI;
			}
		}
	}
}

class ManhattanCIRCUMF extends ManhattanMeasure {
	public ManhattanCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    g.drawString(displayText+"\u2248"
				+MathEqns.chop(8*Math.abs(a[0]-b[0])+8*Math.abs(a[1]-b[1]),GeoPlayground.digits),
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=8*Math.abs(a[0]-b[0])+8*Math.abs(a[1]-b[1]);
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=8*Math.abs(a[0]-b[0])+8*Math.abs(a[1]-b[1]);
		}
	}
}

class ManhattanAREA extends ManhattanMeasure {
	public ManhattanAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    g.drawString(displayText+"\u2248"
				+MathEqns.chop(2*(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]))*(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1])),GeoPlayground.digits),
				SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=2*(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]))*(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]));
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=2*(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]))*(Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]));
		}
	}
}

class ManhattanPERP extends ManhattanLine {
	public ManhattanPERP(int t, LinkedList<GeoConstruct> clickedList) {
		super(t, clickedList);
		x=-clickedList.get(0).y;y=clickedList.get(0).x;
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	    if ((New & getValidNew()) || (!New && getValid())) {
			if (New) getNewXYZ(v1);
			else getXYZ(v1);
			a[0]=v1[0];a[1]=v1[1];
			MathEqns.normalize(a);
			g.drawLine(SZ+(int)(SZ*v2[0]-5*SZ*a[0])+fudge,SZ+(int)(SZ*v2[1]-5*SZ*a[1]),SZ+(int)(SZ*v2[0]+5*SZ*a[0])+fudge,SZ+(int)(SZ*v2[1]+5*SZ*a[1]));
			if (getLabelShown()) g.drawString(displayText,
					SZ+(int)(SZ*(v2[0]+a[0]/2.))+fudge,SZ+(int)(SZ*(v2[1]+a[1]/2.)));
		}	
	}
	public void update() {
		super.update();
		if(getValidNew()){
			v2[0]=-v1[1];
			v2[1]=v1[0];
			setNewXYZ(v2);
		}
	}
}
class ManhattanPARLL0 extends ManhattanLine {
	public ManhattanPARLL0(int t, LinkedList<GeoConstruct> clickedList) {
		super(t, clickedList);
		x=-clickedList.get(0).y;y=clickedList.get(0).x;
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		if ((New & getValidNew()) || (!New && getValid())) {
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		    if (New) getNewXYZ(v1);
			else getXYZ(v1);
			a[0]=v1[0];a[1]=v1[1];
			MathEqns.normalize(a);
			g.drawLine(SZ+(int)(SZ*v2[0]-5*SZ*a[0])+fudge,SZ+(int)(SZ*v2[1]-5*SZ*a[1]),SZ+(int)(SZ*v2[0]+5*SZ*a[0])+fudge,SZ+(int)(SZ*v2[1]+5*SZ*a[1]));
			if (getLabelShown()) g.drawString(displayText,
					SZ+(int)(SZ*(v2[0]+a[0]/2.))+fudge,SZ+(int)(SZ*(v2[1]+a[1]/2.)));
		}	
	}
	public void update() {
		super.update();
		if(getValidNew()){
			v2[0]=v1[0];
			v2[1]=v1[1];
			setNewXYZ(v2);
		}
	}
}
class ManhattanSEGMENT extends ManhattanLine {
	public ManhattanSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}

}

class ManhattanBISECTOR extends ManhattanLine {

	public ManhattanBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {
		super(t, clickedList);
		double[] v1={0,0,0}, v2={0,0,0}, aa={0,0,0},bb={0,0,0},cc={0,0,0};
		clickedList.get(0).getXYZ(aa);
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		EuclideanPoint a=new EuclideanPoint(POINT,tempList,aa);
		clickedList.get(1).getXYZ(bb);
		EuclideanPoint b=new EuclideanPoint(POINT,tempList,bb);
		clickedList.get(2).getXYZ(cc);
		EuclideanPoint c=new EuclideanPoint(POINT,tempList,cc);
		tempList.add(b);	tempList.add(c);
		EuclideanLine d=new EuclideanLine(LINE,tempList);
		tempList.clear();	tempList.add(b);	tempList.add(a);
		EuclideanCircle e=new EuclideanCircle(CIRCLE,tempList);
		EuclideanPoint f1,f2;
		f1=((EuclideanLine)d).intersect(0,(EuclideanCircle)e);
		f2=((EuclideanLine)d).intersect(1,(EuclideanCircle)e);
		tempList.clear();	tempList.add(a);	tempList.add(f1);
		EuclideanPoint g1=new EuclideanMIDPT(MIDPT,tempList,v1);
		tempList.clear();	tempList.add(a);	tempList.add(f2);
		EuclideanPoint g2=new EuclideanMIDPT(MIDPT,tempList,v2);
		tempList.clear();	tempList.add(a);	tempList.add(c);
		if (MathEqns.norm(v1,bb)>MathEqns.norm(v2,bb)) g1=g2;
		tempList.clear();	tempList.add(b);	tempList.add(g1);
		EuclideanLine h=new EuclideanLine(LINE,tempList);
		x=h.getNewX();y=h.getNewY();z=0;
		newX=x;newY=y;newZ=z;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		if (MathEqns.norm(v1,v2)>.00001) {
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if (New) getNewXYZ(v1);
			else getXYZ(v1);
			a[0]=v1[0];a[1]=v1[1];
			MathEqns.normalize(a);
			g.drawLine(SZ+(int)(SZ*v2[0]-5*SZ*a[0])+fudge,SZ+(int)(SZ*v2[1]-5*SZ*a[1]),SZ+(int)(SZ*v2[0]+5*SZ*a[0])+fudge,SZ+(int)(SZ*v2[1]+5*SZ*a[1]));
			if (getLabelShown()) g.drawString(displayText,
					SZ+(int)(SZ*(v2[0]+a[0]/2.))+fudge,SZ+(int)(SZ*(v2[1]+a[1]/2.)));
		}
	}
	public void update() {
		super.update();
		if(getValidNew()){
			double[] aa={0,0,0},bb={0,0,0},cc={0,0,0};
			constList.get(0).getNewXYZ(aa);
			LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
			EuclideanPoint a=new EuclideanPoint(POINT,tempList,aa);
			constList.get(1).getNewXYZ(bb);
			EuclideanPoint b=new EuclideanPoint(POINT,tempList,bb);
			constList.get(2).getNewXYZ(cc);
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
			if (MathEqns.norm(v1)<=.00001) {
				tempList.clear();	tempList.add(a);	tempList.add(b);
				d=new EuclideanLine(LINE,tempList);
				d.update();			d.getNewXYZ(v1);	d.setXYZ(v1);
				tempList.clear();	tempList.add(d);	tempList.add(b);
				h=new EuclideanPERP(PERP,tempList);
				h.update();	h.getNewXYZ(v1);
			}
			if (MathEqns.norm(v1)>.00001) setNewXYZ(v1);
			else setValidNew(false);
		}
	}
}
