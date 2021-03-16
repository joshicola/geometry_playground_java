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

abstract class EuclideanConstruct extends GeoConstruct {
	protected static double scale=0;//Powers of two
	public static final int scaleLimit=5;
	protected double[] v1={0,0,0},v2={0,0,0};//temp vectors

	public EuclideanConstruct(int t, LinkedList<GeoConstruct> clickedList) {
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
	public EuclideanConstruct(int t, LinkedList<GeoConstruct> clickedList,double[] v){
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
			get(0).getXYZ(v);
			get(1).getXYZ(v0);
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

	public abstract EuclideanPoint intersect(int m, EuclideanConstruct a);

	public void setXYZ(double[] vector) {
		if (vector[0]==0 && vector[1]==0) vector[0]+=.0000000001;
		x=vector[0]; y=vector[1]; z=0; 
		newX=x;      newY=y;      newZ=z;
	}

	public void setXYZ(double[] v1, double[] v2) {
		if (MathEqns.norm(v1,v2)>.0001) {
			if(type==LINE || type==SEGMENT){
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
		if ((type<=0 && MathEqns.norm(vector)>.000000001) || type>0) {
		newX=vector[0]; newY=vector[1]; newZ=vector[2];}
		else setValidNew(false);
	}

	public void setNewXYZ(double[] v1, double[] v2) {
		if (MathEqns.norm(v1,v2)>.000001) {
			if(type==LINE || type==SEGMENT){
				newX=-v2[0]+v1[0];newY=-v2[1]+v1[1];newZ=0;
			}
			else if(type==RAY){
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
	public static double getScale(){
		return Math.exp(scale*Math.log(2));
	}
	public static void setScale(double s){
		if(GeoPlayground.model==0||GeoPlayground.model==2)
			if (s*s<.00000001) resetScale();
			else if(Math.abs(scale+s)<=scaleLimit) scale+=s;
	}
	public static void resetScale(){scale=0;}
	public static void rescale(double[] v){
		v[0]*=getScale();
		v[1]*=getScale();
		v[2]=0;
	}
	public static void unscale(double[] v){
		v[0]/=getScale();
		v[1]/=getScale();
		v[2]=0;
	}
	public void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow) {
		MathEqns.transform(fixedObject,this,dragStart,dragNow);
	}
} // end class 


class EuclideanPoint extends EuclideanConstruct{
	protected int sz;
	public EuclideanPoint(int t, LinkedList<GeoConstruct> clickedList,double[] v){super(t,clickedList,v);}

	public EuclideanPoint intersect(int m, EuclideanConstruct a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(a);	tempList.add(this);
		double[] v={0,0,0};
		EuclideanPoint newPoint=new EuclideanPoint(LINEintLINE0,tempList,v);
		return newPoint;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		sz=(int)MathEqns.max(4,SZ/40);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if ((New && getValidNew()) || (!New && getValid())) { 
			if(New) getNewXYZ(v1);
			else getXYZ(v1);
			switch(GeoPlayground.model) {
			case 0:
				rescale(v1);
				break;
			case 1:
				v1[2]=Math.sqrt(1+v1[0]*v1[0]+v1[1]*v1[1]);
				v1[0]/=v1[2];
				v1[1]/=v1[2];
				break;
			case 2:
			case 3:
				v1[2]=v1[0]*v1[0]+v1[1]*v1[1];
				if (v1[2]==0) {v1[0]=.0000000001;v1[2]=.0000000000000001;}
				v1[0]/=v1[2];
				v1[1]/=v1[2];
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
		case 0:
			break;
		case 1:
			if (x*x+y*y==0) xx=.0000000001;
			zz=Math.sqrt(1+xx*xx+yy*yy);
			xx/=zz;		yy/=zz;
			m=2/Math.sqrt(1+xx*xx+yy*yy);
			break;
		case 2:
		case 3:
			if (x*x+y*y==0) xx=.0000000001;
			zz=xx*xx+yy*yy;
			xx/=zz;		yy/=zz;
			m=zz;
			if (m>20) m=20;
			break;
		}
		return ((x-v1[0])*(x-v1[0])+(y-v1[1])*(y-v1[1])<sz*sz/(SZ*SZ)/(getScale()*getScale())*m*m);
	}

	protected void updatePTonLINE(){
		setValidNew(get(0).getValidNew());
		if (getValidNew()) {
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
			if (constList.get(0).getType()==SEGMENT) {
				double[] a={0,0,0},b={0,0,0};
				constList.get(0).get(0).getNewXYZ(a);
				constList.get(0).get(1).getNewXYZ(b);
				if (Math.abs(MathEqns.norm(a,newPt)+MathEqns.norm(newPt,b)-MathEqns.norm(a,b))>.00001) {
					if (MathEqns.norm(a,newPt)>MathEqns.norm(b,newPt)) for (int i=0;i<2;i++) newPt[i]=b[i];
					else for (int i=0;i<2;i++) newPt[i]=a[i];
				}
			}
			if (constList.get(0).getType()==RAY) {
				double[] a={0,0,0},b={0,0,0};
				constList.get(0).get(0).getNewXYZ(a);
				constList.get(0).get(1).getNewXYZ(b);
				if (Math.abs(MathEqns.norm(a,newPt)+MathEqns.norm(a,b)-MathEqns.norm(newPt,b))<.00001)
					for (int i=0;i<2;i++) newPt[i]=a[i];
			}
			setNewXYZ(newPt);
		}
	}

	protected void updatePTonCIRC(){
		setValidNew(get(0).getValidNew());
		if (getValidNew()) {
			double[] vec1={0,0,0},vec2={0,0,0},vec3={0,0,0};
			double r,c;
			constList.get(0).get(0).getNewXYZ(vec1);
			constList.get(0).get(1).getNewXYZ(vec2);
			r=MathEqns.norm(MathEqns.subVec(vec2,vec1));
			vec3=MathEqns.subVec(v1,vec1);
			c=MathEqns.norm(vec3);
			double phi=Math.acos(vec3[0]/c);
			vec2[0]=vec1[0]+r*Math.cos(phi);
			vec2[1]=vec1[1]+Math.signum(vec3[1])*r*Math.sin(phi);
			vec2[2]=0;
			if (r>.0000001)
				setNewXYZ(vec2);
			else setValidNew(false);
		}	
	}

	public void update() {
		boolean nowValid=true;
		for (int i=0;i<constList.size();i++) nowValid = (nowValid && constList.get(i).getValidNew());
		setValidNew(nowValid);
		if (type!=RATIO && type!=SUM && type!=DIFF && type!=PROD) getNewXYZ(v1);
	}
}

class EuclideanLine extends EuclideanConstruct{
	protected double[] a={0,0,0},u={0,0,0},v={0,0,0};
	public EuclideanLine(int t, LinkedList<GeoConstruct> clickedList){
		super(t,clickedList);
			x=clickedList.get(0).x-clickedList.get(1).x;
			y=clickedList.get(0).y-clickedList.get(1).y;
			z=0;
			newX=x;	newY=y;	newZ=0;
	}
	public EuclideanPoint intersect(int m, EuclideanConstruct a){
		EuclideanPoint newPoint;
		if(a.getType()==0)
			newPoint=intersect(m,(EuclideanCircle)a);
		else
			newPoint=intersect(m,(EuclideanLine)a);
		return newPoint;
	}
	public EuclideanPoint intersect(int m, EuclideanLine a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(a);	tempList.add(this);
		EuclideanPoint newPoint=new EuclideanLINEintLINE(LINEintLINE0,tempList,v1);
		newPoint.update();
		newPoint.getNewXYZ(v1);
		newPoint.setXYZ(v1);
		return newPoint;
	}
	public EuclideanPoint intersect(int m, EuclideanCircle a){
		EuclideanPoint newPoint=a.intersect(m,this);
		return newPoint;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if(New){
				getNewXYZ(a);
				constList.get(0).getNewXYZ(v1);
				constList.get(1).getNewXYZ(v2);
			}
			else{
				getXYZ(a);
				constList.get(0).getXYZ(v1);
				constList.get(1).getXYZ(v2);
			}
			if (type!=SEGMENT) {
				MathEqns.normalize(a);
				int m=GeoPlayground.model; 	// we introduce this multiplier so that there are enough sub-intervals to make
				m*=(int)(Math.pow((v1[0]*v1[0]+v1[1]*v1[1])*(v2[0]*v2[0]+v2[1]*v2[1]),.2)); // the line look smooth,
				if (m==0) m=1;				// especially when the points are far from the origin in the inverse model.
				int k=-344*m; if (type==RAY) k=0;
				double t=219.*m;
				if (MathEqns.norm(v1,v2)>.000001)
				for (int i=k;i<344*m;i++) {
					u[0]=v2[0]+Math.tan(i/t)*a[0];
					u[1]=v2[1]+Math.tan(i/t)*a[1];
					v[0]=v2[0]+Math.tan((i+1)/t)*a[0];
					v[1]=v2[1]+Math.tan((i+1)/t)*a[1];
					if (type==RAY) {
						u[0]+=(v1[0]-v2[0]); u[1]+=(v1[1]-v2[1]);
						v[0]+=(v1[0]-v2[0]); v[1]+=(v1[1]-v2[1]);
					}
					switch(GeoPlayground.model){
					case 0:
						rescale(u);		rescale(v);
						break;
					case 1:
						u[2]=Math.sqrt(1+u[0]*u[0]+u[1]*u[1]);
						v[2]=Math.sqrt(1+v[0]*v[0]+v[1]*v[1]);
						u[0]/=u[2];		u[1]/=u[2];
						v[0]/=v[2];		v[1]/=v[2];
						break;
					case 2:
					case 3:
						u[2]=u[0]*u[0]+u[1]*u[1];
						u[0]/=u[2];		u[1]/=u[2];
						v[2]=v[0]*v[0]+v[1]*v[1];
						v[0]/=v[2];		v[1]/=v[2];
						break;
					}
					g.drawLine(SZ+(int)(SZ*u[0])+fudge,SZ+(int)(SZ*u[1]),SZ+(int)(SZ*v[0])+fudge,SZ+(int)(SZ*v[1]));
					if (getLabelShown() && i==100)
						g.drawString(displayText,SZ+(int)(SZ*u[0])+fudge,SZ+(int)(SZ*u[1]));
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
		return Math.abs(MathEqns.dotProduct(v,r))< 0.03/getScale();
	}
	public void update() {
		boolean nowValid=true;
		for (int i=0;i<getSize();i++) if(constList.get(i).getValidNew()==false) nowValid=false;
		setValidNew(nowValid);
		if (nowValid) {
			constList.get(0).getNewXYZ(v1);
			constList.get(1).getNewXYZ(v2);
		}			
		if(nowValid && (type==LINE || type==SEGMENT || type==RAY)){
			if (MathEqns.norm(v1,v2)>.000001) setNewXYZ(v1,v2);
			else setValidNew(false);
		}
	}
}

class EuclideanCircle extends EuclideanConstruct{
	public EuclideanCircle(int t, LinkedList<GeoConstruct> clickedList) {
		super(t,clickedList);
		if (clickedList.get(0).getValid() && clickedList.get(1).getValid()) setValid(true);
	}
	public EuclideanPoint intersect(int m, EuclideanConstruct a){
		EuclideanPoint newPoint;
		if(a.getType()==0)
			newPoint=intersect(m,(EuclideanCircle)a);
		else
			newPoint=intersect(m,(EuclideanLine)a);
		return newPoint;
	}
	public EuclideanPoint intersect(int m, EuclideanLine a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(this);	tempList.add(a);
		EuclideanPoint newPoint=new EuclideanCIRCintLINE(CIRCintLINE0+m,tempList,v1);
		newPoint.setXYZ(getCLxyz(newPoint,a,m));
		return newPoint;
	}
	public EuclideanPoint intersect(int m, EuclideanCircle a){
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		tempList.add(this);	tempList.add(a);
		EuclideanPoint newPoint=new EuclideanCIRCintCIRC(CIRCintCIRC00+m,tempList,v1);
		newPoint.setXYZ(getCCxyz(newPoint,a,m));
		return newPoint;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			double[] center={0,0,0},radial={0,0,0},u={0,0,0},v={0,0,0};
			if(New){
				constList.get(0).getNewXYZ(center);
				constList.get(1).getNewXYZ(radial);
			}
			else{
				constList.get(0).getXYZ(center);
				constList.get(1).getXYZ(radial);
			}
			rescale(center); rescale(radial);
			double r=Math.sqrt((radial[0]-center[0])*(radial[0]-center[0])+(radial[1]-center[1])*(radial[1]-center[1]));
			switch(GeoPlayground.model) {
			case 0:
				g.drawOval(SZ+(int)(SZ*(center[0]-r))+fudge,SZ+(int)(SZ*(center[1]-r)), (int)(SZ*2*r), (int)(SZ*2*r));
				if (getLabelShown())
					g.drawString(displayText,
							SZ+(int)(SZ*(center[0]+.71*r))+fudge,
							SZ+(int)(SZ*(center[1]-.71*r)));
				break;
			case 1:
				for (int i=0;i<88;i++) {
					u[0]=center[0]+r*Math.cos(i/14.);
					u[1]=center[1]+r*Math.sin(i/14.);
					u[2]=Math.sqrt(1+u[0]*u[0]+u[1]*u[1]);
					u[0]/=u[2];		u[1]/=u[2];
					v[0]=center[0]+r*Math.cos((i+1)/14.);
					v[1]=center[1]+r*Math.sin((i+1)/14.);
					v[2]=Math.sqrt(1+v[0]*v[0]+v[1]*v[1]);
					v[0]/=v[2];		v[1]/=v[2];
					g.drawLine(SZ+(int)(SZ*u[0])+fudge,SZ+(int)(SZ*u[1]),SZ+(int)(SZ*v[0])+fudge,SZ+(int)(SZ*v[1]));
					if (getLabelShown() && i==22)
						g.drawString(displayText,SZ+(int)(SZ*u[0])+fudge,SZ+(int)(SZ*u[1]));
				}
				break;
			case 2:
			case 3:
				double denomTemp=center[0]*center[0]+center[1]*center[1]-r*r;
				if (Math.abs(denomTemp)<.001) {
					if (Math.abs(center[1])<.01) {
						g.drawLine(SZ+(int)(SZ/(2*center[0]))+fudge,0,SZ+(int)(SZ/(2*center[0]))+fudge,2*SZ);
					}
					else {
						g.drawLine(0+fudge,SZ+(int)(SZ*(1+2*center[0])/(2*center[1])),
								2*SZ+fudge,SZ+(int)(SZ*(1-2*center[0])/(2*center[1])));
					}
				}
				else {
					double aa=center[0]/denomTemp, bb=center[1]/denomTemp, RR=Math.abs(r/denomTemp);
					g.drawOval(SZ+(int)(SZ*aa-SZ*RR)+fudge,SZ+(int)(SZ*bb-SZ*RR),(int)(SZ*2*RR),(int)(SZ*2*RR));
					if (getLabelShown()) {
						if (aa==0 && bb==0) aa=.000001;
						g.drawString(displayText,SZ+(int)(SZ*aa-SZ*RR*aa/Math.sqrt(aa*aa+bb*bb))+fudge,
								SZ+(int)(SZ*bb-SZ*RR*bb/Math.sqrt(aa*aa+bb*bb)));
					}
				}
				break;	  
			}
		}
	}
	public boolean mouseIsOver(double[] mouse, int SZ){
		double[] axis={0,0,0}, point={0,0,0};
		double radius,dFromAxis;
		constList.get(0).getXYZ(axis);
		constList.get(1).getXYZ(point);
		radius=Math.sqrt((axis[0]-point[0])*(axis[0]-point[0])+(axis[1]-point[1])*(axis[1]-point[1]));
		dFromAxis=Math.sqrt((axis[0]-mouse[0])*(axis[0]-mouse[0])+(axis[1]-mouse[1])*(axis[1]-mouse[1]));
		return Math.abs(radius-dFromAxis)<0.03/getScale();
	}

	public void update() {
		setValidNew(constList.get(0).getValidNew() && constList.get(1).getValidNew());
	}

	public double[] getCLxyz(EuclideanPoint inter, EuclideanConstruct b, int i) {
		double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
		get(0).getXYZ(u); 
		get(1).getXYZ(v);
		b.getXYZ(w);
		b.get(1).getXYZ(z);
		inter.setValid(CircleEqns.calculateEucCL(u,v,w,z,x,(i==0)));
		return x;
	}


	public double[] getNewCLxyz(EuclideanPoint inter,EuclideanConstruct b, int i) {
		double[] u={0,0,0},v={0,0,0},w={0,0,0},z={0,0,0},x={0,0,0};
		get(0).getNewXYZ(u);
		get(1).getNewXYZ(v);
		b.getNewXYZ(w);
		b.get(1).getNewXYZ(z);
		inter.setValidNew(CircleEqns.calculateEucCL(u,v,w,z,x,(i==0)));
		return x;
	}

	public double[] getCCxyz(EuclideanPoint inter,EuclideanConstruct b, int i) {
		double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
		get(0).getXYZ(t);
		get(1).getXYZ(u);
		b.get(0).getXYZ(v);
		b.get(1).getXYZ(w);
		if (t[0]==v[0]) v[0]+=.0000000001;
		inter.setValid(CircleEqns.calculateEucCC(t,u,v,w,x,(i==0)));
		return x;
	}
	public double[] getNewCCxyz(EuclideanPoint inter,EuclideanConstruct b, int i) {
		double[] t={0,0,0},u={0,0,0},v={0,0,0},w={0,0,0},x={0,0,0};
		get(0).getNewXYZ(t);
		get(1).getNewXYZ(u);
		b.get(0).getNewXYZ(v);
		b.get(1).getNewXYZ(w);
		if (t[0]==v[0]) v[0]+=.0000000001;
		inter.setValidNew(CircleEqns.calculateEucCC(t,u,v,w,x,(i==0)));
		return x;
	}  
}

class EuclideanPTonLINE extends EuclideanPoint {
	public EuclideanPTonLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		updatePTonLINE();
	}
}

class EuclideanPTonCIRC extends EuclideanPoint {
	public EuclideanPTonCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		updatePTonCIRC();
	}
}

class EuclideanLINEintLINE extends EuclideanPoint {
	public EuclideanLINEintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
					if (Math.abs(MathEqns.norm(v1,v2)-MathEqns.norm(v1,nm)-MathEqns.norm(nm,v2))>.001)
						setValidNew(false);
				}
				if (constList.get(i).getType()==RAY) {
					constList.get(i).get(0).getNewXYZ(v1);
					constList.get(i).get(1).getNewXYZ(v2);
					if (Math.abs(MathEqns.norm(nm,v2)-MathEqns.norm(v1,nm)-MathEqns.norm(v1,v2))<.001)
						setValidNew(false);
				}
			}
		}
		else setValidNew(false);
	}
}


class EuclideanCIRCintLINE extends EuclideanPoint {
	public EuclideanCIRCintLINE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		setNewXYZ(((EuclideanCircle)constList.get(0)).getNewCLxyz(this,(EuclideanConstruct)constList.get(1),type-CIRCintLINE0));
		for (int i=0;i<2;i++) {
			if (constList.get(i).getType()==SEGMENT) {
				constList.get(i).get(0).getNewXYZ(v1);
				constList.get(i).get(1).getNewXYZ(v2);
				double[] nm={newX,newY,newZ};
				if (Math.abs(MathEqns.norm(v1,v2)-MathEqns.norm(v1,nm)-MathEqns.norm(nm,v2))>.001)
					setValidNew(false);
			}
			if (constList.get(i).getType()==RAY) {
				constList.get(i).get(0).getNewXYZ(v1);
				constList.get(i).get(1).getNewXYZ(v2);
				double[] nm={newX,newY,newZ};
				if (Math.abs(MathEqns.norm(nm,v2)-MathEqns.norm(v1,nm)-MathEqns.norm(v1,v2))<.0001)
					setValidNew(false);
			}
		}
	}
}

class EuclideanCIRCintCIRC extends EuclideanPoint {
	public EuclideanCIRCintCIRC(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		setNewXYZ(((EuclideanCircle)constList.get(0)).getNewCCxyz(this,(EuclideanConstruct)constList.get(1),type-CIRCintCIRC00));
	}
}

class EuclideanMIDPT extends EuclideanPoint {
	public EuclideanMIDPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		constList.get(0).getNewXYZ(v1);
		constList.get(1).getNewXYZ(v2);
		v1[0]=(v1[0]+v2[0])/2;
		v1[1]=(v1[1]+v2[1])/2;
		setNewXYZ(v1);
	}
}

class EuclideanFIXedPT extends EuclideanPoint {
	public EuclideanFIXedPT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
}
class EuclideanReflectPt extends EuclideanPoint {
	public EuclideanReflectPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		tempList.add(constList.get(0).get(0));
		tempList.add(constList.get(0).get(1));
		EuclideanLine l;
		if (constList.get(0).getType()==BISECTOR) {
			tempList.add(constList.get(0).get(2));
			l = new EuclideanBISECTOR(BISECTOR,tempList);
		}
		else if (constList.get(0).getType()==PERP) l = new EuclideanPERP(PERP,tempList);
		else if (constList.get(0).getType()==PARLL0) l = new EuclideanPARLL0(PARLL0,tempList);
		else l = new EuclideanLine(constList.get(0).getType(),tempList);
		l.update();			l.getNewXYZ(w);		l.setXYZ(w);	
		constList.get(1).getNewXYZ(v);
		tempList.clear();
		EuclideanPoint p = new EuclideanPoint(POINT,tempList,v);
		tempList.clear();	tempList.add(l);	tempList.add(p);
		EuclideanPERP a=new EuclideanPERP(PERP,tempList);
		a.update();		a.getNewXYZ(w);		a.setXYZ(w);
		EuclideanPoint b,x0,x1;
		b=((EuclideanLine)a).intersect(0,(EuclideanLine)l);
		b.update();		b.getNewXYZ(u);		b.setXYZ(u);
		if (Math.abs(MathEqns.norm(u,v))<.000001) setNewXYZ(u);	// if point on line, reflection=point. 
		else {														// otherwise...
			tempList.clear();	tempList.add(b);	tempList.add(p);
			EuclideanCircle c=new EuclideanCircle(CIRCLE,tempList);
			x0=((EuclideanLine)a).intersect(0,(EuclideanCircle)c);
			x0.update();	x0.getNewXYZ(v);	x0.setXYZ(v);
			x1=((EuclideanLine)a).intersect(1,(EuclideanCircle)c);
			x1.update();	x1.getNewXYZ(w);	x1.setXYZ(w);
			p.getNewXYZ(u);
			if (MathEqns.norm(u,v)>MathEqns.norm(u,w)) setNewXYZ(v);
			else setNewXYZ(w);
		}
	}
}
class EuclideanTranslatePt extends EuclideanPoint {
	public EuclideanTranslatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
class EuclideanInvertPt extends EuclideanPoint {
	public EuclideanInvertPt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		double[] u={0,0,0},v={0,0,0},w={0,0,0};
		constList.get(0).get(0).getNewXYZ(u);
		constList.get(0).get(1).getNewXYZ(v);
		constList.get(1).getNewXYZ(w);
		if (MathEqns.norm(u,w)<.00000001) setValidNew(false);
		else {
			LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
			setValidNew(true);
			EuclideanPoint a,b,c;
			a=new EuclideanPoint(POINT,tempList,u);
			b=new EuclideanPoint(POINT,tempList,w);
			u[0]+=MathEqns.norm(u,v)*MathEqns.norm(u,v)/MathEqns.norm(u,w);
			c=new EuclideanPoint(POINT,tempList,u);
			tempList.add(a);	tempList.add(b);
			EuclideanLine d = new EuclideanLine(LINE,tempList);
			d.update();			d.getNewXYZ(v);		d.setXYZ(v);
			tempList.clear();	tempList.add(a);	tempList.add(c);
			EuclideanCircle e = new EuclideanCircle(CIRCLE,tempList);
			EuclideanPoint f1,f2;
			f1=((EuclideanLine)d).intersect(0,(EuclideanCircle)e);
			f1.update();	f1.getNewXYZ(v1);	f1.setXYZ(v1);
			f2=((EuclideanLine)d).intersect(1,(EuclideanCircle)e);
			f2.update();	f2.getNewXYZ(v2);	f2.setXYZ(v2);
			if (MathEqns.norm(v1,w)<MathEqns.norm(v2,w)) setNewXYZ(v1);
			else setNewXYZ(v2);
		}
	}
}
class EuclideanRotatePt extends EuclideanPoint {
	public EuclideanRotatePt(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void update() {
		super.update();
		LinkedList<GeoConstruct>tempList=new LinkedList<GeoConstruct>();
		double[] w={0,0,0};
		EuclideanPoint a,b,c,d;
		constList.get(0).get(0).getNewXYZ(w);	a=new EuclideanPoint(POINT,tempList,w);
		constList.get(0).get(1).getNewXYZ(w);	b=new EuclideanPoint(POINT,tempList,w);
		constList.get(0).get(2).getNewXYZ(w);	c=new EuclideanPoint(POINT,tempList,w);
		constList.get(1).getNewXYZ(w);			d=new EuclideanPoint(POINT,tempList,w);
		tempList.add(b);	tempList.add(a);
		EuclideanLine l0 = new EuclideanLine(LINE,tempList);
		l0.update();		l0.getNewXYZ(w);	l0.setXYZ(w);
		tempList.clear();	tempList.add(a);	tempList.add(b);	tempList.add(c);
		EuclideanBISECTOR l1 = new EuclideanBISECTOR(BISECTOR,tempList);
		l1.update();		l1.getNewXYZ(w);	l1.setXYZ(w);
		tempList.clear();	tempList.add(l0);	tempList.add(d);
		EuclideanReflectPt p0 = new EuclideanReflectPt(REFLECT_PT,tempList,w);
		p0.update();		p0.getNewXYZ(w);	p0.setXYZ(w);
		tempList.clear();	tempList.add(l1);	tempList.add(p0);
		EuclideanReflectPt p1 = new EuclideanReflectPt(REFLECT_PT,tempList,w);
		p1.update();		p1.getNewXYZ(w);	setNewXYZ(w);
	}
}
class EuclideanMeasure extends EuclideanPoint {
    protected double[] a={0,0,0},b={0,0,0};
	public EuclideanMeasure(int t, LinkedList<GeoConstruct> clickedList,double[] v) {
		super(t, clickedList, v);
		setDisplayText();
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	  if ((New && getValidNew()) || (!New && getValid())) {
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (New) {
			get(0).getNewXYZ(a);
			get(1).getNewXYZ(b);
		}
		else {
			get(0).getXYZ(a);
			get(1).getXYZ(b);
		}
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);	
	  }
	}

}
class EuclideanCOMMENT extends EuclideanPoint {
	public EuclideanCOMMENT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t,clickedList,v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g,SZ,New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText,SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}
}
class EuclideanCONSTANT extends EuclideanPoint {
	public EuclideanCONSTANT(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.fillRect(SZ+(int)(SZ*v1[0])-sz/2+1+fudge,SZ+(int)(SZ*v1[1])-sz/2+1,sz-2,sz-2);
		g.drawLine(SZ+(int)(SZ*v1[0])+fudge,SZ+(int)(SZ*v1[1]),SZ+(int)(SZ*v1[0])+10+fudge,SZ+(int)(SZ*v1[1])-5);
		g.drawString(displayText+"\u2248"+MathEqns.chop(measureValueNew,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
	}

}
class EuclideanSUM extends EuclideanMeasure {
	public EuclideanSUM(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
class EuclideanRATIO extends EuclideanMeasure {
	public EuclideanRATIO(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
		if (get(1).measureValue==0)		get(1).measureValue=.0000000001;
		if (get(1).measureValueNew==0)	get(1).measureValueNew=.0000000001;
		measureValue=get(0).measureValue/get(1).measureValue;
		measureValueNew=get(0).measureValueNew/get(1).measureValueNew;
	}
}
class EuclideanTRIANGLE extends EuclideanMeasure {
	public EuclideanTRIANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				+MathEqns.chop(Math.sqrt(ss*(ss-ab)*(ss-ac)*(ss-bc)),GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
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
			measureValueNew=Math.sqrt(ss*(ss-ab)*(ss-ac)*(ss-bc));
			get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
			ab=MathEqns.norm(a,b);	ac=MathEqns.norm(a,c);	bc=MathEqns.norm(b,c);
			ss=(ab+ac+bc)/2;
			measureValue=Math.sqrt(ss*(ss-ab)*(ss-ac)*(ss-bc));
		}
	}
}

class EuclideanDISTANCE extends EuclideanMeasure {
	public EuclideanDISTANCE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				+MathEqns.chop(MathEqns.norm(a,b),GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=MathEqns.norm(a,b);
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=MathEqns.norm(a,b);
		}
	}
}

class EuclideanANGLE extends EuclideanMeasure {
	public EuclideanANGLE(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
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

class EuclideanCIRCUMF extends EuclideanMeasure {
	public EuclideanCIRCUMF(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				+MathEqns.chop(2*MathEqns.norm(a,b)*Math.PI,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=2*MathEqns.norm(a,b)*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=2*MathEqns.norm(a,b)*Math.PI;
		}
	}
}

class EuclideanAREA extends EuclideanMeasure {
	public EuclideanAREA(int t, LinkedList<GeoConstruct> clickedList,double[] v) {super(t, clickedList, v);}
	public void draw(Graphics g, int SZ, boolean New) {
		if ((New && getValidNew()) || (!New && getValid())) {
		super.draw(g, SZ, New);
		measureValue=1;
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		g.drawString(displayText+"\u2248"
				+MathEqns.chop(MathEqns.norm(a,b)*MathEqns.norm(a,b)*Math.PI,GeoPlayground.digits),SZ+(int)(SZ*v1[0])+13+fudge,SZ+(int)(SZ*v1[1]));
		}
	}
	public void update() {
		super.update();
		if (measureValue!=0) {
			get(0).getNewXYZ(a);	get(1).getNewXYZ(b);
			measureValueNew=MathEqns.norm(a,b)*MathEqns.norm(a,b)*Math.PI;
			get(0).getXYZ(a);		get(1).getXYZ(b);
			measureValue=MathEqns.norm(a,b)*MathEqns.norm(a,b)*Math.PI;
		}
	}
}

class EuclideanPERP extends EuclideanLine {
	public EuclideanPERP(int t, LinkedList<GeoConstruct> clickedList) {
		super(t, clickedList);
		x=-clickedList.get(0).y;y=clickedList.get(0).x;
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
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
class EuclideanPARLL0 extends EuclideanLine {
	public EuclideanPARLL0(int t, LinkedList<GeoConstruct> clickedList) {
		super(t, clickedList);
		x=clickedList.get(0).x;y=clickedList.get(0).y;
	}
	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
	}
	public void update() {
		super.update();
		if(getValidNew()){
			v2[0]=v1[0];
			v2[1]=v1[1];
			v2[2]=0;
			setNewXYZ(v2);
		}
	}
}

class EuclideanSEGMENT extends EuclideanLine {
	public EuclideanSEGMENT(int t, LinkedList<GeoConstruct> clickedList) {super(t, clickedList);}
	public void draw(Graphics g, int SZ, boolean New) {
		int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
		if (New) {get(0).getNewXYZ(v1); get(1).getNewXYZ(v2);}
		else {get(0).getXYZ(v1); get(1).getXYZ(v2);}
		for (int i=0;i<3;i++) a[i]=v2[i]-v1[i];
		if (MathEqns.norm(v1,v2)>.00001) {
			for (int i=0;i<128;i++) {
				u[0]=v1[0]+i/128.*a[0];		u[1]=v1[1]+i/128.*a[1];		u[2]=0;
				v[0]=v1[0]+(i+1)/128.*a[0];	v[1]=v1[1]+(i+1)/128.*a[1];	v[2]=0;
				switch(GeoPlayground.model) {
				case 0:
					rescale(u);		rescale(v);
					break;
				case 1:
					u[2]=Math.sqrt(1+u[0]*u[0]+u[1]*u[1]);
					u[0]/=u[2];		u[1]/=u[2];
					v[2]=Math.sqrt(1+v[0]*v[0]+v[1]*v[1]);
					v[0]/=v[2];		v[1]/=v[2];
					break;
				case 2:
				case 3:
					u[2]=u[0]*u[0]+u[1]*u[1];
					u[0]/=u[2];		u[1]/=u[2];
					v[2]=v[0]*v[0]+v[1]*v[1];
					v[0]/=v[2];		v[1]/=v[2];
					break;
				}
				g.drawLine(SZ+(int)(SZ*u[0])+fudge,SZ+(int)(SZ*u[1]),SZ+(int)(SZ*v[0])+fudge,SZ+(int)(SZ*v[1]));
				if (getLabelShown() && i==40)
					g.drawString(displayText,SZ+(int)(SZ*u[0])+fudge,SZ+(int)(SZ*u[1]));
				
			}
		}
	}

}

class EuclideanBISECTOR extends EuclideanLine {

	public EuclideanBISECTOR(int t, LinkedList<GeoConstruct> clickedList) {
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
		if (MathEqns.norm(v1,bb)>MathEqns.norm(v2,bb)) {
			g1=g2;
		}
		tempList.clear();	tempList.add(b);	tempList.add(g1);
		EuclideanLine h=new EuclideanLine(LINE,tempList);
		x=h.getNewX();y=h.getNewY();z=0;
		newX=x;newY=y;newZ=z;
	}

	public void draw(Graphics g, int SZ, boolean New) {
		super.draw(g, SZ, New);
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
