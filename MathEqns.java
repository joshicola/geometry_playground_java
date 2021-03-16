import java.util.LinkedList;

public class MathEqns{
  public static double min(double x, double y) { if (x>y) return y; else return x; }
  public static double max(double x, double y) { if (x>y) return x; else return y; }

  public static int round(double x) {
    if (x<0) return -(int)Math.round(Math.abs(x));
	return (int)Math.round(x);
  }
  public static double chop(double x) {
	  int n;
	  boolean neg=false;
	  if (x<0) {neg=true;x*=-1;}
	  if (x<1) {
		  n=(int)(Math.round(Math.pow(10,-Math.floor(Math.log10(x)))));
		  x*=(n*100.);
		  x=(double)(Math.round(x))/(n*100.);
	  }
	  else {
		  n=(int)(Math.round(Math.pow(10,Math.floor(Math.log10(x)))));
		  x*=(100./n);
		  x=(double)(Math.round(x))/(100./n);
	  }
	  if (neg) x*=-1;
	  return x;
  }
  public static double chop(double x, int b) {
	  int n;
	  boolean neg=false;
	  if (x<0) {neg=true;x*=-1;}
	  if (x<1) {
		  n=(int)(Math.round(Math.pow(10,-Math.floor(Math.log10(x)))));
		  x*=(n*Math.pow(10.,b));
		  x=(double)(Math.round(x))/(n*Math.pow(10.,b));
	  }
	  else {
		  n=(int)(Math.round(Math.pow(10,Math.floor(Math.log10(x)))));
		  x*=(Math.pow(10.,b)/n);
		  x=(double)(Math.round(x))/(Math.pow(10.,b)/n);
	  }
	  if (neg) x*=-1;
	  return x;
  }
  public static double norm(double[] v){
    return Math.sqrt(dotProduct(v,v));
  }
  public static double norm(double[] u,double[] v){
    double[] w={0,0,0};
	for (int i=0;i<3;i++) w[i]=u[i]-v[i];
	return norm(w);
  }
  public static double determinant(double[] v0, double[] v1, double[] v2){
	  return v0[0]*(v1[1]*v2[2]-v2[1]*v1[2])-v0[1]*(v1[0]*v2[2]-v2[0]*v1[2])+v0[2]*(v1[0]*v2[1]-v2[0]*v1[1]);
  }
  public static void crossProduct(double[] v1, double[] v2, double[] normal) {
    normal[0]=v1[1]*v2[2]-v2[1]*v1[2];		
    normal[1]=v2[0]*v1[2]-v1[0]*v2[2];		
    normal[2]=v1[0]*v2[1]-v2[0]*v1[1];		
    normalize(normal);
  }
  public static void hypCrossProduct(double[] v1, double[] v2, double[] normal) {
    normal[0]=v1[1]*v2[2]-v2[1]*v1[2];		
    normal[1]=v2[0]*v1[2]-v1[0]*v2[2];		
    normal[2]=v1[0]*v2[1]-v2[0]*v1[1];		
    if (hypProduct(normal,normal)!=0) hypNormalize(normal);
  }
  public static void normalize(double[] v) {
    double r=0;
	r=norm(v);
	if (r!=0)
	for (int i=0;i<3;i++) v[i]/=r;
  }
  public static void hypNormalize(double[] v) {
    double r=0;
	if (hypProduct(v,v)>0) r=Math.sqrt(hypProduct(v,v));
	else r=Math.sqrt(-hypProduct(v,v));
	for (int i=0;i<3;i++) v[i]/=r;
  }
  
  public static double[] scalarProduct(double a, double[] v1){
    for(int i=0;i<3;i++) v1[i]*=a;
    return v1;
  }
  public static double[] addVec(double[] a, double[] b){
    double[] sum={0,0,0};
    sum[0]=a[0]+b[0];
    sum[1]=a[1]+b[1];
    sum[2]=a[2]+b[2];
    return sum;
  }
  public static double[] subVec(double[] a, double[] b){
    double[] diff={0,0,0};
    diff[0]=a[0]-b[0];
    diff[1]=a[1]-b[1];
    diff[2]=a[2]-b[2];
    return diff;
  }
  public static double dotProduct(double[] v1, double[] v2) {
    return v1[2]*v2[2]+v1[1]*v2[1]+v1[0]*v2[0];
  }
  public static double hypProduct(double[] v1, double[] v2) {
    return v1[2]*v2[2]-v1[1]*v2[1]-v1[0]*v2[0];
  }
  public static void rotate(double w, double x, double y, double z, GeoConstruct A) {
	  // this is only called from S & P geometries.
  	double[] vector1={0,0,0};
  	vector1[0]=(1-2*y*y-2*z*z)*A.getX()+
  	               2*(x*y-w*z)*A.getY()+
  				   2*(x*z+w*y)*A.getZ();
  	vector1[1]=    2*(x*y+w*z)*A.getX()+
  	           (1-2*x*x-2*z*z)*A.getY()+
  				   2*(y*z-w*x)*A.getZ();
  	vector1[2]=    2*(x*z-w*y)*A.getX()+
  	               2*(y*z+w*x)*A.getY()+
  			   (1-2*x*x-2*y*y)*A.getZ();
  	A.setNewXYZ(vector1);
  }
  public static void transform(GeoConstruct a,ProjectiveConstruct b,double[] ds,double[] dn){
    // the fixed object is a, the type=point object is b, ds=dragStart, dn=dragNow
	boolean reflect=false;
	double[] a1={0,0,0}, n={0,0,0},
	         dS={ds[0],ds[1],ds[2]},dN={dn[0],dn[1],dn[2]};
	if (a.getType()==0)		// a is a circle
	  a.get(0).getXYZ(a1);	// so we use its center.
	else					// a is a point, or a is a line and XYZ is its dual
	  a.getXYZ(a1);			// so we use the (perhaps dual) point.
	if (a.getType()!=GeoConstruct.SEGMENT) {
	if (a.getType()<0 && dotProduct(a1,dS)*dotProduct(a1,dN)<0) reflect=true;
	crossProduct(a1,dN,n);
	crossProduct(a1,n,dN);
	crossProduct(a1,dS,n);
	crossProduct(a1,n,dS);
	crossProduct(a1,dS,n);
	double theta=Math.acos(dotProduct(dS,dN));
	double phi=0;
	for (int i=0;i<3;i++) phi+=Math.pow((Math.cos(theta)*dS[i]+Math.sin(theta)*n[i]-dN[i]),2);
	if (Math.abs(phi)>.0001) theta*=-1;
	if (reflect) theta+=(Math.PI);
	for (int i=0;i<3;i++) a1[i]*=Math.sin(theta/2);
	rotate(Math.cos(theta/2),a1[0],a1[1],a1[2],b);
	b.getNewXYZ(n);
	}
	else {
		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
		tempList.add(a.get(0));	tempList.add(a.get(1));
		ProjectiveLine theLine = new ProjectiveLine(GeoConstruct.LINE,tempList);
		theLine.update();	theLine.getNewXYZ(n);	theLine.setXYZ(n);
		tempList.clear();
		ProjectivePoint dragS = new ProjectivePoint(GeoConstruct.POINT,tempList,ds);
		ProjectivePoint dragN = new ProjectivePoint(GeoConstruct.POINT,tempList,dn);
		tempList.clear();	tempList.add(dragS);	tempList.add(dragN);
		ProjectiveSEGMENT theDrag = new ProjectiveSEGMENT(GeoConstruct.SEGMENT,tempList);
		theDrag.update();	theDrag.getNewXYZ(n);	theDrag.setXYZ(n);
		ProjectivePoint iS0,iS1,iS2,iS3;
		iS0=((ProjectiveLine)theLine).intersect(0,(ProjectiveLine)theDrag);
		iS0.update();
		iS1=((ProjectiveLine)theLine).intersect(1,(ProjectiveLine)theDrag);
		iS1.update();
		double[] a0={a.get(0).getX(),a.get(0).getY(),a.get(0).getZ()},
				 b0={b.getX(),b.getY(),b.getZ()};
		a.get(1).getXYZ(a1);	tempList.clear();
		ProjectivePoint e0 = new ProjectivePoint(GeoConstruct.POINT,tempList,a0),
					    e1 = new ProjectivePoint(GeoConstruct.POINT,tempList,a1);
		tempList.clear();	tempList.add(e0);	tempList.add(b);
		ProjectiveCircle c0 = new ProjectiveCircle(GeoConstruct.CIRCLE,tempList);
		tempList.clear();	tempList.add(e1);	tempList.add(b);
		ProjectiveCircle c1 = new ProjectiveCircle(GeoConstruct.CIRCLE,tempList);
		iS2=((ProjectiveCircle)c0).intersect(0,(ProjectiveCircle)c1);
		iS2.update(); iS2.getNewXYZ(n);
		iS3=((ProjectiveCircle)c0).intersect(1,(ProjectiveCircle)c1);
		iS3.update();
		if (iS0.getValidNew() || iS1.getValidNew()) {
			if (iS2.getValidNew()) {
				if (norm(n,b0)>.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
		else {
			if (iS2.getValidNew()) {
				if (norm(n,b0)<.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
	}
  }
  public static void transform(GeoConstruct a,SphericalConstruct b,double[] ds,double[] dn){
    // the fixed object is a, the type=point object is b, ds=dragStart, dn=dragNow
	boolean reflect=false;
	double[] a1={0,0,0}, n={0,0,0},
	         dS={ds[0],ds[1],ds[2]},dN={dn[0],dn[1],dn[2]};
	if (a.getType()==0)		// a is a circle
	  a.get(0).getXYZ(a1);	// so we use its center.
	else					// a is a point, or a is a line and XYZ is its dual
	  a.getXYZ(a1);			// so we use the (perhaps dual) point.
	if (a.getType()!=GeoConstruct.SEGMENT) {
	if (a.getType()<0 && dotProduct(a1,dS)*dotProduct(a1,dN)<0) reflect=true;
	crossProduct(a1,dN,n);
	crossProduct(a1,n,dN);
	crossProduct(a1,dS,n);
	crossProduct(a1,n,dS);
	crossProduct(a1,dS,n);
	double theta=Math.acos(dotProduct(dS,dN));
	double phi=0;
	for (int i=0;i<3;i++) phi+=Math.pow((Math.cos(theta)*dS[i]+Math.sin(theta)*n[i]-dN[i]),2);
	if (Math.abs(phi)>.0001) theta*=-1;
	if (reflect) theta+=(Math.PI);
	for (int i=0;i<3;i++) a1[i]*=Math.sin(theta/2);
	rotate(Math.cos(theta/2),a1[0],a1[1],a1[2],b);
	if (reflect) {
	  b.getNewXYZ(n);
	  for (int i=0;i<3;i++) n[i]*=-1;
	  b.setNewXYZ(n);
	}
	}
	else {
		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
		tempList.add(a.get(0));	tempList.add(a.get(1));
		SphericalLine theLine = new SphericalLine(GeoConstruct.LINE,tempList);
		theLine.update();	theLine.getNewXYZ(n);	theLine.setXYZ(n);
		tempList.clear();
		SphericalPoint dragS = new SphericalPoint(GeoConstruct.POINT,tempList,ds);
		SphericalPoint dragN = new SphericalPoint(GeoConstruct.POINT,tempList,dn);
		tempList.clear();	tempList.add(dragS);	tempList.add(dragN);
		SphericalSEGMENT theDrag = new SphericalSEGMENT(GeoConstruct.SEGMENT,tempList);
		theDrag.update();	theDrag.getNewXYZ(n);	theDrag.setXYZ(n);
		SphericalPoint iS0,iS1,iS2,iS3;
		iS0=((SphericalLine)theLine).intersect(0,(SphericalLine)theDrag);
		iS0.update();
		iS1=((SphericalLine)theLine).intersect(1,(SphericalLine)theDrag);
		iS1.update();
		double[] a0={a.get(0).getX(),a.get(0).getY(),a.get(0).getZ()},
				 b0={b.getX(),b.getY(),b.getZ()};
		a.get(1).getXYZ(a1);	tempList.clear();
		SphericalPoint e0 = new SphericalPoint(GeoConstruct.POINT,tempList,a0),
					   e1 = new SphericalPoint(GeoConstruct.POINT,tempList,a1);
		tempList.clear();	tempList.add(e0);	tempList.add(b);
		SphericalCircle c0 = new SphericalCircle(GeoConstruct.CIRCLE,tempList);
		tempList.clear();	tempList.add(e1);	tempList.add(b);
		SphericalCircle c1 = new SphericalCircle(GeoConstruct.CIRCLE,tempList);
		iS2=((SphericalCircle)c0).intersect(0,(SphericalCircle)c1);
		iS2.update(); iS2.getNewXYZ(n);
		iS3=((SphericalCircle)c0).intersect(1,(SphericalCircle)c1);
		iS3.update();
		if (iS0.getValidNew() || iS1.getValidNew()) {
			if (iS2.getValidNew()) {
				if (norm(n,b0)>.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
		else {
			if (iS2.getValidNew()) {
				if (norm(n,b0)<.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
	}
  }
  public static void transform(GeoConstruct a,ToroidalConstruct b,double[] ds,double[] dn){
    // the fixed object is a, the type=point object is b, ds=dragStart, dn=dragNow
	int horiz=0,vert=0;
	double dist=2;
	for (int i=-1;i<2;i++) for (int j=-1;j<2;j++) {
	  double u[]={ds[0],ds[1],0},
	         v[]={dn[0]+i,dn[1]+j,0};
	  if (norm(u,v)<dist) {
	    dist=norm(u,v);
		horiz=i;
		vert=j;
	  }
	}
	dn[0]+=(horiz);dn[1]+=(vert); 
	double[] a1={0,0,0}, b1={0,0,0};
	b.getXYZ(b1);
	a.getXYZ(a1);
	if (a.getType()==0) a.get(0).getXYZ(a1);
	if (a.getType()==0 || a.getType()>0) {
	  // fixedObject=circ or point, transformation=90*n degree rotation
	  double[]  dS={ds[0]-a1[0],ds[1]-a1[1],0},
	            dN={dn[0]-a1[0],dn[1]-a1[1],0},
			    b2={b1[0]-a1[0],b1[1]-a1[1],0};
	  while(dS[0]<-.5) dS[0]+=1;	while(dS[0]>.5) dS[0]-=1;
	  while(dS[1]<-.5) dS[1]+=1;	while(dS[1]>.5) dS[1]-=1;
	  while(dN[0]<-.5) dN[0]+=1;	while(dN[0]>.5) dN[0]-=1;
	  while(dN[1]<-.5) dN[1]+=1;	while(dN[1]>.5) dN[1]-=1;
	  while(b2[0]<-.5) b2[0]+=1;	while(b2[0]>.5) b2[0]-=1;
	  while(b2[1]<-.5) b2[1]+=1;	while(b2[1]>.5) b2[1]-=1;
	  double[] dS1={-dS[1],dS[0],0}, dS3={dS[1],-dS[0],0};
	  if (norm(dS)>0 && norm(dN)>0) {
	    int n=round(2*Math.acos(dotProduct(dS,dN)/norm(dS)/norm(dN))/Math.PI);
	    if (n==1 && norm(dS1,dN)>norm(dS3,dN)) n=3;
	    switch (n) {
	      case 0:
		    break;
		  case 1:
		    b2[2]=b2[0];  b2[0]=-b2[1];  b2[1]=b2[2];
		    break;
		  case 2:
		    b2[0]*=-1; b2[1]*=-1;
		    break;
		  case 3:
		    b2[2]=b2[0];  b2[0]=b2[1];  b2[1]=-b2[2];
		    break;
	    }
	    b1[0]=b2[0]+a1[0]; b1[1]=b2[1]+a1[1];
	  }
	}
	else if (a.getType()!=GeoConstruct.SEGMENT) {
	  //fixedObject=line, transformation=translation.
	  if (a1[0]==0) b1[1]+=(dn[1]-ds[1]);
	  else {
	    double v[]={dn[0]-ds[0],dn[1]-ds[1],0};
	    if (dotProduct(a1,a1)!=0){
	    	b1[0]+=(a1[0]*dotProduct(v,a1)/dotProduct(a1,a1));
	    	b1[1]+=(a1[1]*dotProduct(v,a1)/dotProduct(a1,a1));
	    }
	  }
	}
	else {
		double[] n={b.getX(),b.getY(),0};
		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
		EuclideanPoint bb = new EuclideanPoint(GeoConstruct.POINT,tempList,n);
		tempList.add(a.get(0));	tempList.add(a.get(1));
		EuclideanLine theLine = new EuclideanLine(GeoConstruct.LINE,tempList);
		theLine.update();	theLine.getNewXYZ(n);	theLine.setXYZ(n);
		tempList.clear();
		EuclideanPoint dragS = new EuclideanPoint(GeoConstruct.POINT,tempList,ds);
		EuclideanPoint dragN = new EuclideanPoint(GeoConstruct.POINT,tempList,dn);
		tempList.clear();	tempList.add(dragS);	tempList.add(dragN);
		EuclideanSEGMENT theDrag = new EuclideanSEGMENT(GeoConstruct.SEGMENT,tempList);
		theDrag.update();	theDrag.getNewXYZ(n);	theDrag.setXYZ(n);
		EuclideanPoint iS0,iS1,iS2,iS3;
		iS0=((EuclideanLine)theLine).intersect(0,(EuclideanLine)theDrag);
		iS0.update();
		iS1=((EuclideanLine)theLine).intersect(1,(EuclideanLine)theDrag);
		iS1.update();
		double[] a0={a.get(0).getX(),a.get(0).getY(),a.get(0).getZ()},
				 b0={b.getX(),b.getY(),b.getZ()};
		a.get(1).getXYZ(a1);	tempList.clear();
		EuclideanPoint e0 = new EuclideanPoint(GeoConstruct.POINT,tempList,a0),
					   e1 = new EuclideanPoint(GeoConstruct.POINT,tempList,a1);
		tempList.clear();	tempList.add(e0);	tempList.add(bb);
		EuclideanCircle c0 = new EuclideanCircle(GeoConstruct.CIRCLE,tempList);
		tempList.clear();	tempList.add(e1);	tempList.add(bb);
		EuclideanCircle c1 = new EuclideanCircle(GeoConstruct.CIRCLE,tempList);
		iS2=((EuclideanCircle)c0).intersect(0,(EuclideanCircle)c1);
		iS2.update(); iS2.getNewXYZ(n);
		iS3=((EuclideanCircle)c0).intersect(1,(EuclideanCircle)c1);
		iS3.update();
		if (iS0.getValidNew() || iS1.getValidNew()) {
			if (iS2.getValidNew()) {
				if (norm(n,b0)>.00001) bb.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); bb.setNewXYZ(n);
				}
			}
		}
		else {
			if (iS2.getValidNew()) {
				if (norm(n,b0)<.00001) bb.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); bb.setNewXYZ(n);
				}
			}
		}
		bb.getNewXYZ(b1);
	}
	while (b1[0]<0) b1[0]+=1;	while (b1[0]>=1) b1[0]-=1;
	while (b1[1]<0) b1[1]+=1;	while (b1[1]>=1) b1[1]-=1;
	b.setNewXYZ(b1);
  }
  public static void transform(GeoConstruct a,ConicalConstruct b,double[] ds,double[] dn){}
  public static void transform(GeoConstruct a,ManhattanConstruct b,double[] ds,double[] dn){
	double[] a1={0,0,0}, b1={0,0,0};
	b.getXYZ(b1);
	a.getXYZ(a1);
	if (a.getType()==0) a.get(0).getXYZ(a1);
	if (a.getType()==0 || a.getType()>0) {
		// fixedObject=circ or point, transformation=90*n degree rotation
		double[]  dS={ds[0]-a1[0],ds[1]-a1[1],0},
		          dN={dn[0]-a1[0],dn[1]-a1[1],0},
				  b2={b1[0]-a1[0],b1[1]-a1[1],0};
		double[] dS1={-dS[1],dS[0],0}, dS3={dS[1],-dS[0],0};
		if (norm(dS)>0 && norm(dN)>0) {
		    int n=round(2*Math.acos(dotProduct(dS,dN)/norm(dS)/norm(dN))/Math.PI);
		    if (n==1 && norm(dS1,dN)>norm(dS3,dN)) n=3;
		    switch (n) {
		      case 0:
			    break;
			  case 1:
			    b2[2]=b2[0];  b2[0]=-b2[1];  b2[1]=b2[2];
			    break;
			  case 2:
			    b2[0]*=-1; b2[1]*=-1;
			    break;
			  case 3:
			    b2[2]=b2[0];  b2[0]=b2[1];  b2[1]=-b2[2];
			    break;
		    }
		    b1[0]=b2[0]+a1[0]; b1[1]=b2[1]+a1[1];
		  }
		b.setNewXYZ(b1);
	}
	if (a.getType()<0) {
		double[] n={0,0,0}, dS={ds[0],ds[1],ds[2]}, dN={dn[0],dn[1],dn[2]};
		if (a.getType()!=GeoConstruct.SEGMENT && a.getType()!=GeoConstruct.RAY) {
			a.getXYZ(a1);					// a1 = fixed line
			double[] a2={0,0,0};			// a2 = perp to a1
			a2[0]=dN[0]-dS[0]; a2[1]=dN[1]-dS[1];
		    double[] p1={0,0,0},v0={0,0,0},v1={0,0,0};
			a.get(1).getXYZ(p1);	// p1 = pt on line a1
									// dS = pt on line a2
		    double ss=((dS[1]-p1[1])*a1[0] - (dS[0]-p1[0])*a1[1])/(a2[0]*a1[1]-a2[1]*a1[0]);
			n[0]=a2[0]*ss+dS[0];
			n[1]=a2[1]*ss+dS[1];	// n  = point of intersection of a1 and a2 (through dS)
			n[2]=0;
			LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
			ManhattanPoint dragS = new ManhattanPoint(GeoConstruct.POINT,tempList,ds);
			ManhattanPoint dragN = new ManhattanPoint(GeoConstruct.POINT,tempList,dn);
			tempList.add(a);	tempList.add(dragS);
			ManhattanPERP perp0 = new ManhattanPERP(GeoConstruct.PERP,tempList);
			perp0.update();	perp0.getNewXYZ(v0);	perp0.setXYZ(v0);
			tempList.clear();	tempList.add(a);	tempList.add(dragN);
			ManhattanPERP perp1 = new ManhattanPERP(GeoConstruct.PERP,tempList);
			perp1.update();	perp1.getNewXYZ(v0);	perp1.setXYZ(v0);
			ManhattanPoint iS0,iS1;
			iS0=((ManhattanLine)a).intersect(0,(ManhattanLine)perp0);
			iS0.update();	iS0.getNewXYZ(v0);
			iS1=((ManhattanLine)a).intersect(0,(ManhattanLine)perp1);
			iS1.update();	iS1.getNewXYZ(v1);
			for (int i=0;i<2;i++) b1[i]+=(v1[i]-v0[i]);
			if (a.getType()==GeoConstruct.LINE) {
				a1[0]=a.get(0).getX()-a.get(1).getX();
				a1[1]=a.get(0).getY()-a.get(1).getY();
			}
			if (Math.abs(norm(dS,n)+norm(n,dN)-norm(dS,dN))<.0001) 
			if (Math.abs(a1[0])<.0000001 || Math.abs(a1[1])<.0000001 || 
				(Math.abs(Math.abs(a1[0])-Math.abs(a1[1]))<.0000001 &&
				a.getType()!=GeoConstruct.BISECTOR))
			// something goes buggy when the line is a bisector and its slope is 1 or -1,
			// so we have eliminated that possibility.  this is a hack fix: actually, we
			// should be able to reflect in that extra case, and have just been having
			// difficulty making the reflections stable.
			{ // reflect
				  a2[0]=a1[1];	a2[1]=-a1[0];
				  ss=((b1[1]-p1[1])*a1[0] - (b1[0]-p1[0])*a1[1])/(a2[0]*a1[1]-a2[1]*a1[0]);
				  n[0]=a2[0]*ss+b1[0];
				  n[1]=a2[1]*ss+b1[1];	// n  = point of intersection of a1 and a2 (through b1)
				  n[2]=0;
				  for (int i=0;i<3;i++) {
				    b1[i]-=n[i];
					b1[i]*=-1;//*
					b1[i]+=n[i];
				  }
			}
			b.setNewXYZ(b1);    
		}
		else if (Math.abs(a.getX())<.00000001 || Math.abs(a.getY())<.00000001 ||
				Math.abs(Math.abs(a.getX())-Math.abs(a.getY()))<.00000001) {
			// (a.getType()==SEGMENT || a.getType()==RAY) && slope = 0, 1, -1, or NaN.
			b.getXYZ(b1);
			a.getXYZ(a1);					// a1 = fixed line
			double[] a2={0,0,0};			// a2 = perp to a1
			a2[0]=dN[0]-dS[0]; a2[1]=dN[1]-dS[1];
		    double[] p1={0,0,0};
			a.get(1).getXYZ(p1);	// p1 = pt on line a1
									// dS = pt on line a2
		    double ss=((dS[1]-p1[1])*a1[0] - (dS[0]-p1[0])*a1[1])/(a2[0]*a1[1]-a2[1]*a1[0]);
			n[0]=a2[0]*ss+dS[0];
			n[1]=a2[1]*ss+dS[1];	// n  = point of intersection of a1 and a2 (through dS)
			n[2]=0;
			if (Math.abs(norm(dS,n)+norm(n,dN)-norm(dS,dN))<.0001) {// if segment ds-dn crosses line
				a.get(1).getXYZ(n);
				for (int i=0;i<2;i++) b1[i]-=n[i];
				if (Math.abs(a.getX())<.000000001) b1[0]*=-1;
				if (Math.abs(a.getY())<.000000001) b1[1]*=-1;
				if (Math.abs(a.getX()-a.getY())<.000000001) {
					double temp=b1[0];
					b1[0]=b1[1];
					b1[1]=temp;
				}
				if (Math.abs(a.getX()+a.getY())<.000000001) {
					double temp=b1[0];
					b1[0]=-b1[1];
					b1[1]=-temp;
				}
				for (int i=0;i<2;i++) b1[i]+=n[i];
			}
			b.setNewXYZ(b1);
		}
	}
  }
  public static void transform(GeoConstruct a,EuclideanConstruct b,double[] ds,double[] dn){
    // the fixed object is a, the type=point object is b, ds=dragStart, dn=dragNow
	double[] a1={0,0,0}, s={0,0,0}, n={0,0,0}, b1={0,0,0}, b2={0,0,0},
	         dS={ds[0],ds[1],ds[2]},dN={dn[0],dn[1],dn[2]};
	b.getXYZ(b1);
	if (a.getType()>=0) { // type=circ or Point, transformation=rotation
	  if (a.getType()==0) a.get(0).getXYZ(a1);
	  else a.getXYZ(a1);
	  for (int i=0;i<3;i++) {
	    b1[i]-=a1[i];	dS[i]-=a1[i];	dN[i]-=a1[i];
	  }
	  if (norm(dS)==0) dS[0]+=.00000001;
	  if (norm(dN)==0) dN[0]+=.00000001;
	  for (int i=0;i<2;i++) {
	    s[i]=dS[i]/norm(dS);	n[i]=dN[i]/norm(dN);
	  }
	  double theta=Math.acos(dotProduct(s,n));
	  double phi=Math.pow((Math.cos(theta)*s[0]-Math.sin(theta)*s[1]-n[0]),2)+
	             Math.pow((Math.cos(theta)*s[1]+Math.sin(theta)*s[0]-n[1]),2);
	  if (Math.abs(phi)>.0001) theta*=-1;
	  b2[0]=Math.cos(theta)*b1[0]-Math.sin(theta)*b1[1];
	  b2[1]=Math.cos(theta)*b1[1]+Math.sin(theta)*b1[0];
	  b2[2]=0;
	  for (int i=0;i<3;i++) b1[i]=b2[i]+a1[i];
	  b.setNewXYZ(b1);
	}
	else if (a.getType()!=GeoConstruct.SEGMENT && a.getType()!=GeoConstruct.RAY) {// type=line, transformation=translation*(reflection?)
		a.getXYZ(a1);				// a1 = fixed line
		double[] a2={0,0,0};		// a2 = perp to a1
		a2[0]=dN[0]-dS[0]; a2[1]=dN[1]-dS[1];
	    double[] p1={0,0,0},v0={0,0,0},v1={0,0,0};
		a.get(1).getXYZ(p1);	// p1 = pt on line a1
								// dS = pt on line a2
	    double ss=((dS[1]-p1[1])*a1[0] - (dS[0]-p1[0])*a1[1])/(a2[0]*a1[1]-a2[1]*a1[0]);
		n[0]=a2[0]*ss+dS[0];
		n[1]=a2[1]*ss+dS[1];	// n  = point of intersection of a1 and a2 (through dS)
		n[2]=0;
		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
		EuclideanPoint dragS = new EuclideanPoint(GeoConstruct.POINT,tempList,ds);
		EuclideanPoint dragN = new EuclideanPoint(GeoConstruct.POINT,tempList,dn);
		tempList.add(a);	tempList.add(dragS);
		EuclideanPERP perp0 = new EuclideanPERP(GeoConstruct.PERP,tempList);
		perp0.update();	perp0.getNewXYZ(v0);	perp0.setXYZ(v0);
		tempList.clear();	tempList.add(a);	tempList.add(dragN);
		EuclideanPERP perp1 = new EuclideanPERP(GeoConstruct.PERP,tempList);
		perp1.update();	perp1.getNewXYZ(v0);	perp1.setXYZ(v0);
		EuclideanPoint iS0,iS1;
		iS0=((EuclideanLine)a).intersect(0,(EuclideanLine)perp0);
		iS0.update();	iS0.getNewXYZ(v0);
		iS1=((EuclideanLine)a).intersect(0,(EuclideanLine)perp1);
		iS1.update();	iS1.getNewXYZ(v1);
		for (int i=0;i<2;i++) b1[i]+=(v1[i]-v0[i]);
		if (Math.abs(norm(dS,n)+norm(n,dN)-norm(dS,dN))<.0001) { // reflect
		  a2[0]=a1[1];	a2[1]=-a1[0];
		  ss=((b1[1]-p1[1])*a1[0] - (b1[0]-p1[0])*a1[1])/(a2[0]*a1[1]-a2[1]*a1[0]);
		  n[0]=a2[0]*ss+b1[0];
		  n[1]=a2[1]*ss+b1[1];	// n  = point of intersection of a1 and a2 (through b1)
		  n[2]=0;
		  for (int i=0;i<3;i++) {
		    b1[i]-=n[i];
			b1[i]*=-1;
			b1[i]+=n[i];
		  }
		}
		b.setNewXYZ(b1);
       
	}
	else { // type == SEGMENT || type == RAY
		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
		tempList.add(a.get(0));	tempList.add(a.get(1));
		EuclideanLine theLine = new EuclideanLine(GeoConstruct.LINE,tempList);
		theLine.update();	theLine.getNewXYZ(n);	theLine.setXYZ(n);
		tempList.clear();
		EuclideanPoint dragS = new EuclideanPoint(GeoConstruct.POINT,tempList,ds);
		EuclideanPoint dragN = new EuclideanPoint(GeoConstruct.POINT,tempList,dn);
		tempList.clear();	tempList.add(dragS);	tempList.add(dragN);
		EuclideanSEGMENT theDrag = new EuclideanSEGMENT(GeoConstruct.SEGMENT,tempList);
		theDrag.update();	theDrag.getNewXYZ(n);	theDrag.setXYZ(n);
		EuclideanPoint iS0,iS1,iS2,iS3;
		iS0=((EuclideanLine)theLine).intersect(0,(EuclideanLine)theDrag);
		iS0.update();
		iS1=((EuclideanLine)theLine).intersect(1,(EuclideanLine)theDrag);
		iS1.update();
		double[] a0={a.get(0).getX(),a.get(0).getY(),a.get(0).getZ()},
				 b0={b.getX(),b.getY(),b.getZ()};
		a.get(1).getXYZ(a1);	tempList.clear();
		EuclideanPoint e0 = new EuclideanPoint(GeoConstruct.POINT,tempList,a0),
					   e1 = new EuclideanPoint(GeoConstruct.POINT,tempList,a1);
		tempList.clear();	tempList.add(e0);	tempList.add(b);
		EuclideanCircle c0 = new EuclideanCircle(GeoConstruct.CIRCLE,tempList);
		tempList.clear();	tempList.add(e1);	tempList.add(b);
		EuclideanCircle c1 = new EuclideanCircle(GeoConstruct.CIRCLE,tempList);
		iS2=((EuclideanCircle)c0).intersect(0,(EuclideanCircle)c1);
		iS2.update(); iS2.getNewXYZ(n);
		iS3=((EuclideanCircle)c0).intersect(1,(EuclideanCircle)c1);
		iS3.update();
		if (iS0.getValidNew() || iS1.getValidNew()) {
			if (iS2.getValidNew()) {
				if (norm(n,b0)>.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
		else {
			if (iS2.getValidNew()) {
				if (norm(n,b0)<.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
	}
	
  }
  public static void transform(GeoConstruct a,HyperConstruct b,double[] ds,double[] dn){
    // the fixed object is a, the type=point object is b, ds=dragStart, dn=dragNow
	double[] a1={0,0,0}, n={0,0,0}, b1={0,0,0},
	         dS={ds[0],ds[1],ds[2]},dN={dn[0],dn[1],dn[2]};
	if (a.getType()==0) a.get(0).getXYZ(a1);
	else a.getXYZ(a1);
	b.getXYZ(b1);
	if (a.getType()>=0) {// rotation: fixedObject = pt or circ
	  hypTranslate(a1,dS,n);
	  for (int i=0;i<2;i++) dS[i]=n[i];
	  hypTranslate(a1,dN,n);
	  for (int i=0;i<2;i++) dN[i]=n[i];
	  dS[2]=0;			dN[2]=0;
	  normalize(dS);	normalize(dN);
	  n[0]=-dS[1];		n[1]=dS[0];
	  double theta=Math.acos(dotProduct(dS,dN));
	  double phi=0;
	  for (int i=0;i<2;i++) phi+=Math.pow((Math.cos(theta)*dS[i]+Math.sin(theta)*n[i]-dN[i]),2);
	  if (Math.abs(phi)>.0001) theta*=-1;
	  hypTranslate(a1,b1,n);
	  b1[0]=Math.cos(theta)*n[0]-Math.sin(theta)*n[1];
	  b1[1]=Math.cos(theta)*n[1]+Math.sin(theta)*n[0];
	  b1[2]=n[2];
	  a1[0]*=-1;	a1[1]*=-1;
	  hypTranslate(a1,b1,n);
	  a1[0]*=-1;	a1[1]*=-1;
	  b.setNewXYZ(n);
	}
	else  if (a.getType()!=GeoConstruct.SEGMENT && a.getType()!=GeoConstruct.RAY) {// translation: fixedObject = line
	  boolean reflect=false;
	  double[] b2={0,0,0},b3={0,0,0},pt={0,0,0},s1={0,0,0},n1={0,0,0};
	  hypPerp(a1,dS,n);	hypLineIntLine(a1,n,s1);// s1 = proj of dS on a1
	  hypPerp(a1,dN,n);	hypLineIntLine(a1,n,n1);// n1 = proj of dN on a1
	  hypCrossProduct(dS,dN,n);					// n = line through dS and dN
	  if (n[0]*n[0]+n[1]*n[1]+n[2]*n[2]==0) b.setNewXYZ(b1);
	  else {
	  if (hypLineIntLine(n,a1,pt) &&
	      (Math.abs(acosh(hypProduct(dS,pt))+acosh(hypProduct(pt,dN))-acosh(hypProduct(dS,dN)))<.001
		  ||
		  Math.abs(acosh(hypProduct(s1,pt))+acosh(hypProduct(pt,n1))-acosh(hypProduct(s1,n1)))<.001))
		reflect=true;
	  hypPerp(a1,b1,n);				// n  = perp to a1 through b1
	  hypLineIntLine(n,a1,b2);		// b2 = proj of b1 on a1
	  hypTranslate(s1,b1,b2);		// b2 = b1 translated down dS
	  hypTranslate(s1,n1,n);		// n  = dN translated down dS
	  n[0]*=-1;		n[1]*=-1;
	  s1[0]*=-1;	s1[1]*=-1;
	  hypTranslate(n,b2,b3);		// b3 = b2 translated up n
	  hypTranslate(s1,b3,b2);		// b2 = b3 translated up dS
	  if (reflect) {
	    hypPerp(a1,b2,b1); hypLineIntLine(a1,b1,b3);
		hypTranslate(b3,b2,n);
		n[0]*=-1; n[1]*=-1;
		b3[0]*=-1;b3[1]*=-1;
		hypTranslate(b3,n,b2);
	  }
	  b.setNewXYZ(b2);				// this is the translated (& reflected) point.
	  }
	}
	else { // type == SEGMENT or RAY
		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
		tempList.add(a.get(0));	tempList.add(a.get(1));
		HyperLine theLine = new HyperLine(GeoConstruct.LINE,tempList);
		theLine.update();	theLine.getNewXYZ(n);	theLine.setXYZ(n);
		tempList.clear();
		HyperPoint dragS = new HyperPoint(GeoConstruct.POINT,tempList,ds);
		HyperPoint dragN = new HyperPoint(GeoConstruct.POINT,tempList,dn);
		tempList.clear();	tempList.add(dragS);	tempList.add(dragN);
		HyperSEGMENT theDrag = new HyperSEGMENT(GeoConstruct.SEGMENT,tempList);
		theDrag.update();	theDrag.getNewXYZ(n);	theDrag.setXYZ(n);
		HyperPoint iS0,iS1,iS2,iS3;
		iS0=((HyperLine)theLine).intersect(0,(HyperLine)theDrag);
		iS0.update();
		iS1=((HyperLine)theLine).intersect(1,(HyperLine)theDrag);
		iS1.update();
		double[] a0={a.get(0).getX(),a.get(0).getY(),a.get(0).getZ()},
				 b0={b.getX(),b.getY(),b.getZ()};
		a.get(1).getXYZ(a1);	tempList.clear();
		HyperPoint	e0 = new HyperPoint(GeoConstruct.POINT,tempList,a0),
					e1 = new HyperPoint(GeoConstruct.POINT,tempList,a1);
		tempList.clear();	tempList.add(e0);	tempList.add(b);
		HyperCircle c0 = new HyperCircle(GeoConstruct.CIRCLE,tempList);
		tempList.clear();	tempList.add(e1);	tempList.add(b);
		HyperCircle c1 = new HyperCircle(GeoConstruct.CIRCLE,tempList);
		iS2=((HyperCircle)c0).intersect(0,(HyperCircle)c1);
		iS2.update(); iS2.getNewXYZ(n);
		iS3=((HyperCircle)c0).intersect(1,(HyperCircle)c1);
		iS3.update();
		if (iS0.getValidNew() || iS1.getValidNew()) {
			if (iS2.getValidNew()) {
				if (norm(n,b0)>.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
		else {
			if (iS2.getValidNew()) {
				if (norm(n,b0)<.00001) b.setNewXYZ(n);
				else {
					iS3.getNewXYZ(n); b.setNewXYZ(n);
				}
			}
		}
	}
  }
  public static void hypPerp(double[] ln, double[] pt, double[] perp) {
    double sr=Math.sqrt(-(pt[1]*pt[1]*ln[0]*ln[0])+pt[2]*pt[2]*ln[0]*ln[0]+2*pt[0]*pt[1]*ln[0]*ln[1]-pt[0]*pt[0]*ln[1]*ln[1]+2*pt[0]*pt[2]*ln[0]*ln[2]+pt[0]*pt[0]*ln[2]*ln[2]+(pt[2]*ln[1]+pt[1]*ln[2])*(pt[2]*ln[1]+pt[1]*ln[2]));
	perp[2]=(pt[1]*ln[0]-pt[0]*ln[1])/sr;
	perp[1]=-(pt[2]*ln[0]+pt[0]*ln[2])/sr;
	perp[0]=(pt[2]*ln[1]+pt[1]*ln[2])/sr;
  }
  public static boolean hypParallel(double[] line, double[] point, double[] parallel, boolean bit) {
	  double[] inftyVector={0,0,0};
	  int x=1;
	  if (bit) x=-1;
	  if ((line[1]*line[1])!=(line[2]*line[2]) && line[2]!=0) {
		  inftyVector[0]=1;
		  inftyVector[1]=(-line[0]*line[1]+x*line[2]*Math.sqrt(line[0]*line[0]+line[1]*line[1]-line[2]*line[2]))
		  		/(line[1]*line[1]-line[2]*line[2]);
		  inftyVector[2]=-line[0]/line[2]*inftyVector[0]-line[1]/line[2]*inftyVector[1];
		  MathEqns.crossProduct(inftyVector,point,parallel);
		  MathEqns.hypNormalize(parallel);
		  return true;
	  }
	  else return false;
  }
  public static boolean hypLineIntLine(double[] l1, double[] l2, double[] pt) {
    hypCrossProduct(l1,l2,pt);
    hypNormalize(pt);
    if (hypProduct(pt,pt)<=0 || norm(pt)>1e6) return false;
    else return true;
  }
  public static void hypTranslate(double[] ds, double[] v1, double[] v2) {
	// this is the hyperbolic translation from the point ds to the origin.
	// to go backwards, use (-ds[0],-ds[1],ds[2]) instead of ds.
	// v2=v1-ds
	if (ds[0]*ds[0]+ds[1]*ds[1]==0) {
	  for (int i=0;i<3;i++) v2[i]=v1[i];
	  return;
	}
    double[][] dsToOrigin={{(ds[0]*ds[0]*ds[2]+ds[1]*ds[1])/(ds[0]*ds[0]+ds[1]*ds[1]),
	                        (ds[0]*ds[1]*(ds[2]-1))/(ds[0]*ds[0]+ds[1]*ds[1]),-ds[0]},
	                       {(ds[0]*ds[1]*(ds[2]-1))/(ds[0]*ds[0]+ds[1]*ds[1]),
						    (ds[1]*ds[1]*ds[2]+ds[0]*ds[0])/(ds[0]*ds[0]+ds[1]*ds[1]),-ds[1]},
						   {-ds[0],-ds[1],ds[2]}};
	for (int i=0;i<3;i++)
	  v2[i]=v1[0]*dsToOrigin[0][i]+v1[1]*dsToOrigin[1][i]+v1[2]*dsToOrigin[2][i];
  }
  public static void sphTranslate(double[] ds, double[] v1, double[] v2) {
	// this is the spherical translation from the point ds to (0,0,1).
	// v2=v1-ds
	if (ds[0]*ds[0]+ds[1]*ds[1]==0) {
	  for (int i=0;i<3;i++) v2[i]=v1[i];
	  return;
	}
    double[] norm={0,0,0},temp={0,0,1};
	crossProduct(temp,ds,norm);
	double theta=Math.acos(MathEqns.dotProduct(temp,ds));
    double w=Math.cos(theta/2);
    for (int i=0;i<3;i++) norm[i]*=Math.sin(theta/2);
  	v2[0]=(1-2*norm[1]*norm[1]-2*norm[2]*norm[2])*v1[0]+
  	                2*(norm[0]*norm[1]-w*norm[2])*v1[1]+
  				    2*(norm[0]*norm[2]+w*norm[1])*v1[2];
  	v2[1]=			2*(norm[0]*norm[1]+w*norm[2])*v1[0]+
		  (1-2*norm[0]*norm[0]-2*norm[2]*norm[2])*v1[1]+
					2*(norm[1]*norm[2]-w*norm[0])*v1[2];
  	v2[2]=			2*(norm[0]*norm[2]-w*norm[1])*v1[0]+
					2*(norm[1]*norm[2]+w*norm[0])*v1[1]+
		  (1-2*norm[0]*norm[0]-2*norm[1]*norm[1])*v1[2];
  }
  
  public static void makeStandard(double[] vector) {
    if (vector[2]<0 || (vector[2]==0 && vector[1]<0) || 
	    (vector[2]==0 && vector[1]==0 && vector[0]<0))
	  for (int i=0;i<3;i++) vector[i]*=-1;
  }
  public static double acosh(double x) {
    return Math.log(x+Math.sqrt(x*x-1));
  }
  
  public static double eucAngle(double[] a, double[] b, double[] c) {
    return Math.acos(dotProduct(subVec(a,b),subVec(c,b))/norm(subVec(a,b))/norm(subVec(c,b)))/Math.PI*180;
  }
  public static double hypAngle(double[] a, double[] b, double[] c) {
    double[] d={0,0,0};
	hypTranslate(b,a,d);
	for (int i=0;i<3;i++) a[i]=d[i];
	hypTranslate(b,c,d);
	for (int i=0;i<3;i++) c[i]=d[i];
	a[2]=0;
	c[2]=0;
	for (int i=0;i<3;i++) d[i]=0;
	return eucAngle(a,d,c);
  }
  public static double sphAngle(double[] a, double[] b, double[] c) {
    double[] d={0,0,0};
	hypTranslate(b,a,d);
	for (int i=0;i<3;i++) a[i]=d[i];
	hypTranslate(b,c,d);
	for (int i=0;i<3;i++) c[i]=d[i];
	a[2]=0;
	c[2]=0;
	for (int i=0;i<3;i++) d[i]=0;
	return eucAngle(a,d,c);
  }
}
