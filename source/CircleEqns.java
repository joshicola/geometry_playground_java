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
 
import java.util.LinkedList;

//This class is to store all those ugly calculations made for the circle

public class CircleEqns {
  public static boolean calculateCL(double[] u,double[] v,double[] w, double[] x, boolean bit) {
    double discriminant=-((u[1]*w[0]-u[0]*w[1])*(u[1]*w[0]-u[0]*w[1])*(u[1]*u[1]*((-1+v[1]*v[1])*w[0]*w[0]+v[1]*v[1]*w[1]*w[1]+(-1+v[1]*v[1])*w[2]*w[2])+u[2]*u[2]*((-1+v[2]*v[2])*(w[0]*w[0]+w[1]*w[1])+v[2]*v[2]*w[2]*w[2])+u[0]*u[0]*(-w[1]*w[1]-w[2]*w[2]+v[0]*v[0]*(w[0]*w[0]+w[1]*w[1]+w[2]*w[2]))+2*u[0]*u[2]*(w[0]*w[2]+v[0]*v[2]*(w[0]*w[0]+w[1]*w[1]+w[2]*w[2]))+2*u[1]*(u[0]*(w[0]*w[1]+v[0]*v[1]*(w[0]*w[0]+w[1]*w[1]+w[2]*w[2]))+u[2]*(w[1]*w[2]+v[1]*v[2]*(w[0]*w[0]+w[1]*w[1]+w[2]*w[2])))));
  if (discriminant>=0) {//circ with ctr u & pt v intersects line with normal w (Proj & Sph)
    double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
    x[0]=(-(u[1]*(u[1]*v[1]+u[2]*v[2])*w[0]*w[0]*(u[1]*w[1]+u[2]*w[2]))-u[0]*u[0]*u[0]*v[0]*w[1]*(w[1]*w[1]+w[2]*w[2])+u[0]*w[0]*(u[2]*u[2]*v[2]*w[1]*w[2]+u[1]*u[1]*(-(v[0]*w[0]*w[1])+v[1]*(2*w[1]*w[1]+w[2]*w[2]))+u[1]*u[2]*((-(v[0]*w[0])+v[1]*w[1])*w[2]+v[2]*(2*w[1]*w[1]+w[2]*w[2])))+u[0]*u[0]*(-(u[2]*w[1]*(-(v[0]*w[0]*w[2])+v[2]*(w[1]*w[1]+w[2]*w[2])))+u[1]*(-(v[1]*w[1]*(w[1]*w[1]+w[2]*w[2]))+v[0]*w[0]*(2*w[1]*w[1]+w[2]*w[2])))-u[2]*w[1]*sr+u[1]*w[2]*sr)/((u[1]*w[0]-u[0]*w[1])*(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1])-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]+w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]+w[2]*w[2])));
    x[1]=(u[0]*u[0]*u[0]*v[0]*w[0]*w[1]*w[1]+u[1]*(u[1]*v[1]+u[2]*v[2])*w[0]*(-(u[2]*w[1]*w[2])+u[1]*(w[0]*w[0]+w[2]*w[2]))+u[0]*u[0]*w[1]*(u[2]*w[1]*(v[2]*w[0]+v[0]*w[2])+u[1]*(v[1]*w[0]*w[1]-v[0]*(2*w[0]*w[0]+w[2]*w[2])))+u[0]*(u[2]*u[2]*v[2]*w[1]*w[1]*w[2]-u[1]*u[2]*w[1]*((v[0]*w[0]-v[1]*w[1])*w[2]+v[2]*(2*w[0]*w[0]+w[2]*w[2]))+u[1]*u[1]*(v[0]*w[0]*(w[0]*w[0]+w[2]*w[2])-v[1]*w[1]*(2*w[0]*w[0]+w[2]*w[2])))+u[2]*w[0]*sr-u[0]*w[2]*sr)/((u[1]*w[0]-u[0]*w[1])*(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1])-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]+w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]+w[2]*w[2])));
    x[2]=-(((u[0]*v[0]+u[1]*v[1]+u[2]*v[2])*(-(u[2]*(w[0]*w[0]+w[1]*w[1]))+(u[0]*w[0]+u[1]*w[1])*w[2])+sr)/(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1])-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]+w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]+w[2]*w[2])));
    return true;
  }
  else return false;
  }
  public static boolean calculateCC0(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
    double discriminant=-((t[1]*v[0]-t[0]*v[1])*(t[1]*v[0]-t[0]*v[1])*(t[0]*t[0]*(-v[1]*v[1]-v[2]*v[2]+u[0]*u[0]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])-2*u[0]*v[0]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])+(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))+t[1]*t[1]*(-v[2]*v[2]+u[1]*u[1]*(v[1]*v[1]+v[2]*v[2])+v[0]*v[0]*(-1+u[1]*u[1]+w[0]*w[0])-2*u[1]*v[1]*(v[1]*w[1]+v[2]*w[2])+(v[1]*w[1]+v[2]*w[2])*(v[1]*w[1]+v[2]*w[2])+2*v[0]*w[0]*(v[1]*(-u[1]+w[1])+v[2]*w[2]))+t[2]*t[2]*(v[0]*v[0]*(-1+u[2]*u[2]+w[0]*w[0])+v[1]*v[1]*(-1+u[2]*u[2]+w[1]*w[1])+v[2]*v[2]*(u[2]-w[2])*(u[2]-w[2])+2*v[1]*v[2]*w[1]*(-u[2]+w[2])+2*v[0]*w[0]*(v[1]*w[1]+v[2]*(-u[2]+w[2])))+2*t[0]*t[2]*(-(v[0]*(u[2]*(v[0]*w[0]+v[1]*w[1])+v[2]*(-1+u[2]*w[2])))+u[0]*(u[2]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])-v[2]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])))+2*t[1]*(t[0]*(-(v[0]*(v[1]*(-1+u[1]*w[1])+u[1]*(v[0]*w[0]+v[2]*w[2])))+u[0]*(u[1]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])-v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])))+t[2]*(-(v[1]*(u[2]*(v[0]*w[0]+v[1]*w[1])+v[2]*(-1+u[2]*w[2])))+u[1]*(u[2]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])-v[2]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))))));
  if (discriminant>=0) {//circ with ctr t & pt u intersects circ with ctr v & pt w (Proj & Sph)
    double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
    x[0]=(-(t[0]*t[0]*t[0]*u[0]*v[1]*(v[1]*v[1]+v[2]*v[2]))+t[1]*v[0]*v[0]*(-(t[1]*t[2]*(u[2]*v[1]+u[1]*v[2]))+t[1]*t[1]*(v[0]*w[0]+v[1]*(-u[1]+w[1])+v[2]*w[2])+t[2]*t[2]*(v[0]*w[0]+v[1]*w[1]+v[2]*(-u[2]+w[2])))-t[0]*v[0]*(t[2]*t[2]*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(-u[2]+w[2]))+t[1]*t[1]*(u[0]*v[0]*v[1]-u[1]*(2*v[1]*v[1]+v[2]*v[2])+2*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))+t[1]*t[2]*(-(u[2]*(2*v[1]*v[1]+v[2]*v[2]))+v[2]*(v[0]*(u[0]+w[0])+v[1]*(-u[1]+w[1])+v[2]*w[2])))+t[0]*t[0]*(t[2]*v[1]*(-(u[2]*(v[1]*v[1]+v[2]*v[2]))+v[2]*(v[0]*(u[0]+w[0])+v[1]*w[1]+v[2]*w[2]))+t[1]*(u[0]*v[0]*(2*v[1]*v[1]+v[2]*v[2])+v[1]*(-(u[1]*(v[1]*v[1]+v[2]*v[2]))+v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))))-t[2]*v[1]*sr+t[1]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1])-2*t[0]*t[2]*v[0]*v[2]-2*t[1]*v[1]*(t[0]*v[0]+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]+v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]+v[2]*v[2])));
    x[1]=(-(t[0]*t[0]*t[0]*v[1]*v[1]*(v[0]*(-u[0]+w[0])+v[1]*w[1]+v[2]*w[2]))+t[1]*v[0]*(t[1]*t[1]*u[1]*(v[0]*v[0]+v[2]*v[2])+t[2]*t[2]*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(-u[2]+w[2]))+t[1]*t[2]*(u[2]*(v[0]*v[0]+v[2]*v[2])-v[2]*(v[0]*w[0]+v[1]*(u[1]+w[1])+v[2]*w[2])))+t[0]*(-(t[2]*t[2]*v[1]*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(-u[2]+w[2])))-t[1]*t[1]*(-(u[0]*v[0]*(v[0]*v[0]+v[2]*v[2]))+u[1]*v[1]*(2*v[0]*v[0]+v[2]*v[2])+v[0]*v[0]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))+t[1]*t[2]*v[1]*(-(u[2]*(2*v[0]*v[0]+v[2]*v[2]))+v[2]*(v[0]*(-u[0]+w[0])+v[1]*(u[1]+w[1])+v[2]*w[2])))+t[0]*t[0]*v[1]*(t[2]*v[1]*(u[2]*v[0]+u[0]*v[2])+t[1]*(-(u[0]*(2*v[0]*v[0]+v[2]*v[2]))+v[0]*(u[1]*v[1]+2*v[0]*w[0]+2*v[1]*w[1]+2*v[2]*w[2])))+t[2]*v[0]*sr-t[0]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1])-2*t[0]*t[2]*v[0]*v[2]-2*t[1]*v[1]*(t[0]*v[0]+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]+v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]+v[2]*v[2])));
    x[2]=(t[2]*t[2]*u[2]*(v[0]*v[0]+v[1]*v[1])+v[2]*(-(t[0]*t[1]*(u[1]*v[0]+u[0]*v[1]))+t[0]*t[0]*(v[0]*(-u[0]+w[0])+v[1]*w[1]+v[2]*w[2])+t[1]*t[1]*(v[0]*w[0]+v[1]*(-u[1]+w[1])+v[2]*w[2]))-t[2]*(t[0]*(-(u[0]*(v[0]*v[0]+v[1]*v[1]))+v[0]*(v[0]*w[0]+v[1]*w[1]+v[2]*(u[2]+w[2])))+t[1]*(-(u[1]*(v[0]*v[0]+v[1]*v[1]))+v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(u[2]+w[2]))))-sr)/(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1])-2*t[0]*t[2]*v[0]*v[2]-2*t[1]*v[1]*(t[0]*v[0]+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]+v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]+v[2]*v[2]));
    return true;
  }
  else return false;
  }
  public static boolean calculateCC1(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
    double discriminant=-((t[1]*v[0]-t[0]*v[1])*(t[1]*v[0]-t[0]*v[1])*(t[0]*t[0]*(-v[1]*v[1]-v[2]*v[2]+u[0]*u[0]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])+2*u[0]*v[0]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])+(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))+t[1]*t[1]*(-v[2]*v[2]+u[1]*u[1]*(v[1]*v[1]+v[2]*v[2])+v[0]*v[0]*(-1+u[1]*u[1]+w[0]*w[0])+2*u[1]*v[1]*(v[1]*w[1]+v[2]*w[2])+(v[1]*w[1]+v[2]*w[2])*(v[1]*w[1]+v[2]*w[2])+2*v[0]*w[0]*(v[1]*(u[1]+w[1])+v[2]*w[2]))+t[2]*t[2]*(v[0]*v[0]*(-1+u[2]*u[2]+w[0]*w[0])+v[1]*v[1]*(-1+u[2]*u[2]+w[1]*w[1])+2*v[1]*v[2]*w[1]*(u[2]+w[2])+v[2]*v[2]*(u[2]+w[2])*(u[2]+w[2])+2*v[0]*w[0]*(v[1]*w[1]+v[2]*(u[2]+w[2])))+2*t[0]*t[2]*(v[0]*(v[2]+u[2]*v[0]*w[0]+u[2]*v[1]*w[1]+u[2]*v[2]*w[2])+u[0]*(u[2]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])+v[2]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])))+2*t[1]*(t[0]*(v[0]*(v[1]+u[1]*v[0]*w[0]+u[1]*v[1]*w[1]+u[1]*v[2]*w[2])+u[0]*(u[1]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])+v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2])))+t[2]*(v[1]*(v[2]+u[2]*v[0]*w[0]+u[2]*v[1]*w[1]+u[2]*v[2]*w[2])+u[1]*(u[2]*(v[0]*v[0]+v[1]*v[1]+v[2]*v[2])+v[2]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))))));
  if (discriminant>=0) {//circ with ctr t & pt u intersects circ with ctr v & pt w (Proj only)
    double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
    x[0]=(t[0]*t[0]*t[0]*u[0]*v[1]*(v[1]*v[1]+v[2]*v[2])+t[1]*v[0]*v[0]*(t[1]*t[2]*(u[2]*v[1]+u[1]*v[2])+t[1]*t[1]*(v[0]*w[0]+v[1]*(u[1]+w[1])+v[2]*w[2])+t[2]*t[2]*(v[0]*w[0]+v[1]*w[1]+v[2]*(u[2]+w[2])))+t[0]*v[0]*(-(t[2]*t[2]*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(u[2]+w[2])))+t[1]*t[1]*(u[0]*v[0]*v[1]-u[1]*(2*v[1]*v[1]+v[2]*v[2])-2*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))-t[1]*t[2]*(u[2]*(2*v[1]*v[1]+v[2]*v[2])+v[2]*(v[0]*(-u[0]+w[0])+v[1]*(u[1]+w[1])+v[2]*w[2])))+t[0]*t[0]*(t[2]*v[1]*(u[2]*(v[1]*v[1]+v[2]*v[2])+v[2]*(v[0]*(-u[0]+w[0])+v[1]*w[1]+v[2]*w[2]))+t[1]*(-(u[0]*v[0]*(2*v[1]*v[1]+v[2]*v[2]))+v[1]*(u[1]*(v[1]*v[1]+v[2]*v[2])+v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))))-t[2]*v[1]*sr+t[1]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1])-2*t[0]*t[2]*v[0]*v[2]-2*t[1]*v[1]*(t[0]*v[0]+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]+v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]+v[2]*v[2])));
    x[1]=-((t[0]*t[0]*t[0]*v[1]*v[1]*(v[0]*(u[0]+w[0])+v[1]*w[1]+v[2]*w[2])+t[1]*v[0]*(t[1]*t[1]*u[1]*(v[0]*v[0]+v[2]*v[2])-t[2]*t[2]*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(u[2]+w[2]))+t[1]*t[2]*(u[2]*(v[0]*v[0]+v[2]*v[2])+v[2]*(v[0]*w[0]+v[1]*(-u[1]+w[1])+v[2]*w[2])))+t[0]*(t[2]*t[2]*v[1]*v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(u[2]+w[2]))+t[1]*t[1]*(u[0]*v[0]*(v[0]*v[0]+v[2]*v[2])-u[1]*v[1]*(2*v[0]*v[0]+v[2]*v[2])+v[0]*v[0]*(v[0]*w[0]+v[1]*w[1]+v[2]*w[2]))+t[1]*t[2]*v[1]*(-(u[2]*(2*v[0]*v[0]+v[2]*v[2]))-v[2]*(v[0]*(u[0]+w[0])+v[1]*(-u[1]+w[1])+v[2]*w[2])))+t[0]*t[0]*v[1]*(t[2]*v[1]*(u[2]*v[0]+u[0]*v[2])-t[1]*(u[0]*(2*v[0]*v[0]+v[2]*v[2])+v[0]*(-(u[1]*v[1])+2*v[0]*w[0]+2*v[1]*w[1]+2*v[2]*w[2])))-t[2]*v[0]*sr+t[0]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1])-2*t[0]*t[2]*v[0]*v[2]-2*t[1]*v[1]*(t[0]*v[0]+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]+v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]+v[2]*v[2]))));
    x[2]=-((t[2]*t[2]*u[2]*(v[0]*v[0]+v[1]*v[1])-v[2]*(t[0]*t[1]*(u[1]*v[0]+u[0]*v[1])+t[0]*t[0]*(v[0]*(u[0]+w[0])+v[1]*w[1]+v[2]*w[2])+t[1]*t[1]*(v[0]*w[0]+v[1]*(u[1]+w[1])+v[2]*w[2]))+t[2]*(t[0]*(u[0]*(v[0]*v[0]+v[1]*v[1])+v[0]*(v[0]*w[0]+v[1]*w[1]+v[2]*(-u[2]+w[2])))+t[1]*(u[1]*(v[0]*v[0]+v[1]*v[1])+v[1]*(v[0]*w[0]+v[1]*w[1]+v[2]*(-u[2]+w[2]))))+sr)/(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1])-2*t[0]*t[2]*v[0]*v[2]-2*t[1]*v[1]*(t[0]*v[0]+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]+v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]+v[2]*v[2])));
    return true;
  }
  else return false;
  }
  public static boolean calculateHypCL(double[] u, double[] v, double[] w, double[] x, boolean bit) {
    double discriminant=(u[1]*w[0]-u[0]*w[1])*(u[1]*w[0]-u[0]*w[1])*(u[2]*u[2]*((-1+v[2]*v[2])*w[0]*w[0]+(-1+v[2]*v[2])*w[1]*w[1]-v[2]*v[2]*w[2]*w[2])+u[1]*u[1]*((1+v[1]*v[1])*w[0]*w[0]-w[2]*w[2]+v[1]*v[1]*(w[1]*w[1]-w[2]*w[2]))+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2]+v[0]*v[0]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))-2*u[0]*u[2]*(w[0]*w[2]+v[0]*v[2]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))+2*u[1]*(u[0]*(-(w[0]*w[1])+v[0]*v[1]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))-u[2]*(w[1]*w[2]+v[1]*v[2]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))));
  if (discriminant>=0) {//circ with ctr u & pt v intersects line with normal w (Hyp only)
    double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
    x[0]=-((u[0]*u[1]*u[1]*v[0]*w[0]*w[0]*w[1]+u[1]*u[1]*u[1]*v[1]*w[0]*w[0]*w[1]-u[1]*u[1]*u[2]*v[2]*w[0]*w[0]*w[1]-2*u[0]*u[0]*u[1]*v[0]*w[0]*w[1]*w[1]-2*u[0]*u[1]*u[1]*v[1]*w[0]*w[1]*w[1]+2*u[0]*u[1]*u[2]*v[2]*w[0]*w[1]*w[1]+u[0]*u[0]*u[0]*v[0]*w[1]*w[1]*w[1]+u[0]*u[0]*u[1]*v[1]*w[1]*w[1]*w[1]-u[0]*u[0]*u[2]*v[2]*w[1]*w[1]*w[1]+u[0]*u[1]*u[2]*v[0]*w[0]*w[0]*w[2]+u[1]*u[1]*u[2]*v[1]*w[0]*w[0]*w[2]-u[1]*u[2]*u[2]*v[2]*w[0]*w[0]*w[2]-u[0]*u[0]*u[2]*v[0]*w[0]*w[1]*w[2]-u[0]*u[1]*u[2]*v[1]*w[0]*w[1]*w[2]+u[0]*u[2]*u[2]*v[2]*w[0]*w[1]*w[2]+u[0]*u[0]*u[1]*v[0]*w[0]*w[2]*w[2]+u[0]*u[1]*u[1]*v[1]*w[0]*w[2]*w[2]-u[0]*u[1]*u[2]*v[2]*w[0]*w[2]*w[2]-u[0]*u[0]*u[0]*v[0]*w[1]*w[2]*w[2]-u[0]*u[0]*u[1]*v[1]*w[1]*w[2]*w[2]+u[0]*u[0]*u[2]*v[2]*w[1]*w[2]*w[2]+u[2]*w[1]*sr+u[1]*w[2]*sr)/((u[1]*w[0]-u[0]*w[1])*(-(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1]))-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]-w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2]))));
	x[1]=(u[0]*u[1]*u[1]*v[0]*w[0]*w[0]*w[0]+u[1]*u[1]*u[1]*v[1]*w[0]*w[0]*w[0]-u[1]*u[1]*u[2]*v[2]*w[0]*w[0]*w[0]-2*u[0]*u[0]*u[1]*v[0]*w[0]*w[0]*w[1]-2*u[0]*u[1]*u[1]*v[1]*w[0]*w[0]*w[1]+2*u[0]*u[1]*u[2]*v[2]*w[0]*w[0]*w[1]+u[0]*u[0]*u[0]*v[0]*w[0]*w[1]*w[1]+u[0]*u[0]*u[1]*v[1]*w[0]*w[1]*w[1]-u[0]*u[0]*u[2]*v[2]*w[0]*w[1]*w[1]-u[0]*u[1]*u[2]*v[0]*w[0]*w[1]*w[2]-u[1]*u[1]*u[2]*v[1]*w[0]*w[1]*w[2]+u[1]*u[2]*u[2]*v[2]*w[0]*w[1]*w[2]+u[0]*u[0]*u[2]*v[0]*w[1]*w[1]*w[2]+u[0]*u[1]*u[2]*v[1]*w[1]*w[1]*w[2]-u[0]*u[2]*u[2]*v[2]*w[1]*w[1]*w[2]-u[0]*u[1]*u[1]*v[0]*w[0]*w[2]*w[2]-u[1]*u[1]*u[1]*v[1]*w[0]*w[2]*w[2]+u[1]*u[1]*u[2]*v[2]*w[0]*w[2]*w[2]+u[0]*u[0]*u[1]*v[0]*w[1]*w[2]*w[2]+u[0]*u[1]*u[1]*v[1]*w[1]*w[2]*w[2]-u[0]*u[1]*u[2]*v[2]*w[1]*w[2]*w[2]+u[2]*w[0]*sr+u[0]*w[2]*sr)/((u[1]*w[0]-u[0]*w[1])*(-(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1]))-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]-w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2])));
	x[2]=(u[0]*u[2]*v[0]*w[0]*w[0]+u[1]*u[2]*v[1]*w[0]*w[0]-u[2]*u[2]*v[2]*w[0]*w[0]+u[0]*u[2]*v[0]*w[1]*w[1]+u[1]*u[2]*v[1]*w[1]*w[1]-u[2]*u[2]*v[2]*w[1]*w[1]+u[0]*u[0]*v[0]*w[0]*w[2]+u[0]*u[1]*v[1]*w[0]*w[2]-u[0]*u[2]*v[2]*w[0]*w[2]+u[0]*u[1]*v[0]*w[1]*w[2]+u[1]*u[1]*v[1]*w[1]*w[2]-u[1]*u[2]*v[2]*w[1]*w[2]+sr)/(-(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1]))-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]-w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2]));
	return true;
  }
  else return false;
  }
  public static boolean calculateHypCC(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
    double discriminant=(t[1]*v[0]-t[0]*v[1])*(t[1]*v[0]-t[0]*v[1])*(t[2]*t[2]*(v[0]*v[0]*(-1+u[2]*u[2]-w[0]*w[0])+v[1]*v[1]*(-1+u[2]*u[2]-w[1]*w[1])-v[2]*v[2]*(u[2]-w[2])*(u[2]-w[2])+2*v[1]*v[2]*w[1]*(-u[2]+w[2])-2*v[0]*w[0]*(u[2]*v[2]+v[1]*w[1]-v[2]*w[2]))+t[0]*t[0]*(-v[2]*v[2]+u[0]*u[0]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2])+v[0]*v[0]*w[0]*w[0]+v[1]*v[1]*(1+w[1]*w[1])-2*v[0]*v[2]*w[0]*w[2]+v[2]*v[2]*w[2]*w[2]+2*v[1]*w[1]*(v[0]*w[0]-v[2]*w[2])-2*u[0]*v[0]*(v[0]*w[0]+v[1]*w[1]-v[2]*w[2]))+t[1]*t[1]*(-v[2]*v[2]+u[1]*u[1]*(v[1]*v[1]-v[2]*v[2])+v[0]*v[0]*(1+u[1]*u[1]+w[0]*w[0])+v[1]*v[1]*w[1]*w[1]-2*v[1]*v[2]*w[1]*w[2]+v[2]*v[2]*w[2]*w[2]+2*u[1]*v[1]*(-(v[1]*w[1])+v[2]*w[2])-2*v[0]*w[0]*(u[1]*v[1]-v[1]*w[1]+v[2]*w[2]))-2*t[0]*t[2]*(-(v[0]*(v[2]+u[2]*v[0]*w[0]+u[2]*v[1]*w[1]-u[2]*v[2]*w[2]))+u[0]*(u[2]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2])+v[2]*(-(v[0]*w[0])-v[1]*w[1]+v[2]*w[2])))+2*t[1]*(t[0]*(-(v[0]*(v[1]+u[1]*v[0]*w[0]+u[1]*v[1]*w[1]-u[1]*v[2]*w[2]))+u[0]*(u[1]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2])-v[1]*(v[0]*w[0]+v[1]*w[1]-v[2]*w[2])))+t[2]*(v[1]*(v[2]+u[2]*v[0]*w[0]+u[2]*v[1]*w[1]-u[2]*v[2]*w[2])+u[1]*(-(u[2]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2]))+v[2]*(v[0]*w[0]+v[1]*w[1]-v[2]*w[2])))));
  if (discriminant>=0) { //circ with ctr t & pt u intersects circ with ctr v & pt w (Hyp only)
    double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
    x[0]=(-(t[0]*t[1]*t[1]*u[0]*v[0]*v[0]*v[1])-t[1]*t[1]*t[1]*u[1]*v[0]*v[0]*v[1]+t[1]*t[1]*t[2]*u[2]*v[0]*v[0]*v[1]+2*t[0]*t[0]*t[1]*u[0]*v[0]*v[1]*v[1]+2*t[0]*t[1]*t[1]*u[1]*v[0]*v[1]*v[1]-2*t[0]*t[1]*t[2]*u[2]*v[0]*v[1]*v[1]-t[0]*t[0]*t[0]*u[0]*v[1]*v[1]*v[1]-t[0]*t[0]*t[1]*u[1]*v[1]*v[1]*v[1]+t[0]*t[0]*t[2]*u[2]*v[1]*v[1]*v[1]+t[0]*t[1]*t[2]*u[0]*v[0]*v[0]*v[2]+t[1]*t[1]*t[2]*u[1]*v[0]*v[0]*v[2]-t[1]*t[2]*t[2]*u[2]*v[0]*v[0]*v[2]-t[0]*t[0]*t[2]*u[0]*v[0]*v[1]*v[2]-t[0]*t[1]*t[2]*u[1]*v[0]*v[1]*v[2]+t[0]*t[2]*t[2]*u[2]*v[0]*v[1]*v[2]-t[0]*t[0]*t[1]*u[0]*v[0]*v[2]*v[2]-t[0]*t[1]*t[1]*u[1]*v[0]*v[2]*v[2]+t[0]*t[1]*t[2]*u[2]*v[0]*v[2]*v[2]+t[0]*t[0]*t[0]*u[0]*v[1]*v[2]*v[2]+t[0]*t[0]*t[1]*u[1]*v[1]*v[2]*v[2]-t[0]*t[0]*t[2]*u[2]*v[1]*v[2]*v[2]+t[1]*t[1]*t[1]*v[0]*v[0]*v[0]*w[0]-t[1]*t[2]*t[2]*v[0]*v[0]*v[0]*w[0]-2*t[0]*t[1]*t[1]*v[0]*v[0]*v[1]*w[0]+t[0]*t[2]*t[2]*v[0]*v[0]*v[1]*w[0]+t[0]*t[0]*t[1]*v[0]*v[1]*v[1]*w[0]+t[0]*t[1]*t[2]*v[0]*v[0]*v[2]*w[0]-t[0]*t[0]*t[2]*v[0]*v[1]*v[2]*w[0]+t[1]*t[1]*t[1]*v[0]*v[0]*v[1]*w[1]-t[1]*t[2]*t[2]*v[0]*v[0]*v[1]*w[1]-2*t[0]*t[1]*t[1]*v[0]*v[1]*v[1]*w[1]+t[0]*t[2]*t[2]*v[0]*v[1]*v[1]*w[1]+t[0]*t[0]*t[1]*v[1]*v[1]*v[1]*w[1]+t[0]*t[1]*t[2]*v[0]*v[1]*v[2]*w[1]-t[0]*t[0]*t[2]*v[1]*v[1]*v[2]*w[1]-t[1]*t[1]*t[1]*v[0]*v[0]*v[2]*w[2]+t[1]*t[2]*t[2]*v[0]*v[0]*v[2]*w[2]+2*t[0]*t[1]*t[1]*v[0]*v[1]*v[2]*w[2]-t[0]*t[2]*t[2]*v[0]*v[1]*v[2]*w[2]-t[0]*t[0]*t[1]*v[1]*v[1]*v[2]*w[2]-t[0]*t[1]*t[2]*v[0]*v[2]*v[2]*w[2]+t[0]*t[0]*t[2]*v[1]*v[2]*v[2]*w[2]-t[2]*v[1]*sr+t[1]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(-(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1]))+2*t[0]*t[2]*v[0]*v[2]+2*t[1]*v[1]*(-(t[0]*v[0])+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]-v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]-v[2]*v[2])));
	x[1]=(t[0]*t[1]*t[1]*u[0]*v[0]*v[0]*v[0]+t[1]*t[1]*t[1]*u[1]*v[0]*v[0]*v[0]-t[1]*t[1]*t[2]*u[2]*v[0]*v[0]*v[0]-2*t[0]*t[0]*t[1]*u[0]*v[0]*v[0]*v[1]-2*t[0]*t[1]*t[1]*u[1]*v[0]*v[0]*v[1]+2*t[0]*t[1]*t[2]*u[2]*v[0]*v[0]*v[1]+t[0]*t[0]*t[0]*u[0]*v[0]*v[1]*v[1]+t[0]*t[0]*t[1]*u[1]*v[0]*v[1]*v[1]-t[0]*t[0]*t[2]*u[2]*v[0]*v[1]*v[1]+t[0]*t[1]*t[2]*u[0]*v[0]*v[1]*v[2]+t[1]*t[1]*t[2]*u[1]*v[0]*v[1]*v[2]-t[1]*t[2]*t[2]*u[2]*v[0]*v[1]*v[2]-t[0]*t[0]*t[2]*u[0]*v[1]*v[1]*v[2]-t[0]*t[1]*t[2]*u[1]*v[1]*v[1]*v[2]+t[0]*t[2]*t[2]*u[2]*v[1]*v[1]*v[2]-t[0]*t[1]*t[1]*u[0]*v[0]*v[2]*v[2]-t[1]*t[1]*t[1]*u[1]*v[0]*v[2]*v[2]+t[1]*t[1]*t[2]*u[2]*v[0]*v[2]*v[2]+t[0]*t[0]*t[1]*u[0]*v[1]*v[2]*v[2]+t[0]*t[1]*t[1]*u[1]*v[1]*v[2]*v[2]-t[0]*t[1]*t[2]*u[2]*v[1]*v[2]*v[2]-t[0]*t[1]*t[1]*v[0]*v[0]*v[0]*w[0]+2*t[0]*t[0]*t[1]*v[0]*v[0]*v[1]*w[0]-t[1]*t[2]*t[2]*v[0]*v[0]*v[1]*w[0]-t[0]*t[0]*t[0]*v[0]*v[1]*v[1]*w[0]+t[0]*t[2]*t[2]*v[0]*v[1]*v[1]*w[0]+t[1]*t[1]*t[2]*v[0]*v[0]*v[2]*w[0]-t[0]*t[1]*t[2]*v[0]*v[1]*v[2]*w[0]-t[0]*t[1]*t[1]*v[0]*v[0]*v[1]*w[1]+2*t[0]*t[0]*t[1]*v[0]*v[1]*v[1]*w[1]-t[1]*t[2]*t[2]*v[0]*v[1]*v[1]*w[1]-t[0]*t[0]*t[0]*v[1]*v[1]*v[1]*w[1]+t[0]*t[2]*t[2]*v[1]*v[1]*v[1]*w[1]+t[1]*t[1]*t[2]*v[0]*v[1]*v[2]*w[1]-t[0]*t[1]*t[2]*v[1]*v[1]*v[2]*w[1]+t[0]*t[1]*t[1]*v[0]*v[0]*v[2]*w[2]-2*t[0]*t[0]*t[1]*v[0]*v[1]*v[2]*w[2]+t[1]*t[2]*t[2]*v[0]*v[1]*v[2]*w[2]+t[0]*t[0]*t[0]*v[1]*v[1]*v[2]*w[2]-t[0]*t[2]*t[2]*v[1]*v[1]*v[2]*w[2]-t[1]*t[1]*t[2]*v[0]*v[2]*v[2]*w[2]+t[0]*t[1]*t[2]*v[1]*v[2]*v[2]*w[2]+t[2]*v[0]*sr-t[0]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(-(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1]))+2*t[0]*t[2]*v[0]*v[2]+2*t[1]*v[1]*(-(t[0]*v[0])+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]-v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]-v[2]*v[2])));
	x[2]=(t[0]*t[2]*u[0]*v[0]*v[0]+t[1]*t[2]*u[1]*v[0]*v[0]-t[2]*t[2]*u[2]*v[0]*v[0]+t[0]*t[2]*u[0]*v[1]*v[1]+t[1]*t[2]*u[1]*v[1]*v[1]-t[2]*t[2]*u[2]*v[1]*v[1]-t[0]*t[0]*u[0]*v[0]*v[2]-t[0]*t[1]*u[1]*v[0]*v[2]+t[0]*t[2]*u[2]*v[0]*v[2]-t[0]*t[1]*u[0]*v[1]*v[2]-t[1]*t[1]*u[1]*v[1]*v[2]+t[1]*t[2]*u[2]*v[1]*v[2]-t[0]*t[2]*v[0]*v[0]*w[0]-t[1]*t[2]*v[0]*v[1]*w[0]+t[0]*t[0]*v[0]*v[2]*w[0]+t[1]*t[1]*v[0]*v[2]*w[0]-t[0]*t[2]*v[0]*v[1]*w[1]-t[1]*t[2]*v[1]*v[1]*w[1]+t[0]*t[0]*v[1]*v[2]*w[1]+t[1]*t[1]*v[1]*v[2]*w[1]+t[0]*t[2]*v[0]*v[2]*w[2]+t[1]*t[2]*v[1]*v[2]*w[2]-t[0]*t[0]*v[2]*v[2]*w[2]-t[1]*t[1]*v[2]*v[2]*w[2]+sr)/(-(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1]))+2*t[0]*t[2]*v[0]*v[2]+2*t[1]*v[1]*(-(t[0]*v[0])+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]-v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]-v[2]*v[2]));
	return true;
  }
  else return false;
  }
  
  public static boolean calculateEucCL(double[] u, double[] v, double[] w,double[] z, double[] x, boolean bit) {
    if (w[0]==0) {
	  x[0]=z[0];
	  x[2]=0;
	  double discriminant=(u[0]-v[0])*(u[0]-v[0])+(u[1]-v[1])*(u[1]-v[1])-(z[0]-u[0])*(z[0]-u[0]);
	  if (discriminant>=0) {
	    if (bit) x[1]=u[1]+Math.sqrt(discriminant);
		else x[1]=u[1]-Math.sqrt(discriminant);
		return true;
	  }
	  else return false;
	}
	double m=w[1]/w[0],yint=z[1]-m*z[0], r=Math.sqrt((u[0]-v[0])*(u[0]-v[0])+(u[1]-v[1])*(u[1]-v[1]));
    //The solution to the intersection point of a circle and a line becomes a quadradic with the following terms
    double  Quad=m*m+1,
    		Lin= 2*(m*(yint-u[1])-u[0]),
    		Con=(yint-u[1])*(yint-u[1])+u[0]*u[0]-r*r;

    double Dis=Lin*Lin-4*Con*Quad;

    if(Dis>0){
      x[0]=-1*Lin;
      if (bit)  x[0]+=Math.sqrt(Dis);
      else 		x[0]-=Math.sqrt(Dis);
      x[0]/=(2*Quad);
      x[1]=m*x[0]+yint;
      x[2]=0;
      return true;
    }
    else return false;
  }

  public static boolean calculateEucCC(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
  	double r1=Math.sqrt((u[0]-t[0])*(u[0]-t[0])+(u[1]-t[1])*(u[1]-t[1])),
		   r2=Math.sqrt((v[0]-w[0])*(v[0]-w[0])+(v[1]-w[1])*(v[1]-w[1]));
	double  A=(4*t[0]*t[0]+4*t[1]*t[1]-8*t[0]*v[0]+4*v[0]*v[0]-8*t[1]*v[1]+4*v[1]*v[1]),
			B=(4*r1*r1*t[1]-4*r2*r2*t[1]-4*t[0]*t[0]*t[1]-4*t[1]*t[1]*t[1]+8*t[0]*t[1]*v[0]-4*t[1]*v[0]*v[0]-4*r1*r1*v[1]+4*r2*r2*v[1]-4*t[0]*t[0]*v[1]+4*t[1]*t[1]*v[1]+8*t[0]*v[0]*v[1]-4*v[0]*v[0]*v[1]+4*t[1]*v[1]*v[1]-4*v[1]*v[1]*v[1]),
			C=(r1*r1*r1*r1-2*r1*r1*r2*r2+r2*r2*r2*r2-2*r1*r1*t[0]*t[0]-2*r2*r2*t[0]*t[0]+t[0]*t[0]*t[0]*t[0]-2*r1*r1*t[1]*t[1]+2*r2*r2*t[1]*t[1]+2*t[0]*t[0]*t[1]*t[1]+t[1]*t[1]*t[1]*t[1]+4*r1*r1*t[0]*v[0]+4*r2*r2*t[0]*v[0]-4*t[0]*t[0]*t[0]*v[0]-4*t[0]*t[1]*t[1]*v[0]-2*r1*r1*v[0]*v[0]-2*r2*r2*v[0]*v[0]+6*t[0]*t[0]*v[0]*v[0]+2*t[1]*t[1]*v[0]*v[0]-4*t[0]*v[0]*v[0]*v[0]+v[0]*v[0]*v[0]*v[0]+2*r1*r1*v[1]*v[1]-2*r2*r2*v[1]*v[1]+2*t[0]*t[0]*v[1]*v[1]-2*t[1]*t[1]*v[1]*v[1]-4*t[0]*v[0]*v[1]*v[1]+2*v[0]*v[0]*v[1]*v[1]+v[1]*v[1]*v[1]*v[1]);	 
    double discriminant=B*B-4*A*C;
    
    if (discriminant>=0) { //circ with ctr t & pt u intersects circ with ctr v & pt w
      double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
   
      x[0]=(1/(2*t[0]-2*v[0]))*(-r1*r1+r2*r2+t[0]*t[0]+t[1]*t[1]-v[0]*v[0]-v[1]*v[1]+(4*r1*r1*t[1]*t[1]-4*r2*r2*t[1]*t[1]-4*t[0]*t[0]*t[1]*t[1]-4*t[1]*t[1]*t[1]*t[1]+8*t[0]*t[1]*t[1]*v[0]-4*t[1]*t[1]*v[0]*v[0]-8*r1*r1*t[1]*v[1]+8*r2*r2*t[1]*v[1]+8*t[1]*t[1]*t[1]*v[1]+4*r1*r1*v[1]*v[1]-4*r2*r2*v[1]*v[1]+4*t[0]*t[0]*v[1]*v[1]-8*t[0]*v[0]*v[1]*v[1]+4*v[0]*v[0]*v[1]*v[1]-8*t[1]*v[1]*v[1]*v[1]+4*v[1]*v[1]*v[1]*v[1]+(v[1]-t[1])*sr)/A);
      x[1]=(-1*B+sr)/(2*A);
      return true;
    }
    else return false;
  }
  public static boolean calculateManCL(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
	//circ with cntr t & pt u intersects line with slope v[1]/v[0] and pt w in pt x
	x[0]=100;	x[1]=100;	x[2]=0;
	if (v[0]==0 && v[1]==0) return false;
	else if (Math.abs(v[0]-v[1])<.00001) {// slope==+1
		EuclideanPoint a,b,c,d,e;
  		EuclideanLine l,m;
  		double[] v1={0,0,0},v2={0,0,0},z={w[0]+v[0],w[1]+v[1],0};
  		double[][] n={{0,0,0},{0,0,0}};
  		double r=Math.abs(u[0]-t[0])+Math.abs(u[1]-t[1]);
  		LinkedList<GeoConstruct> temp=new LinkedList<GeoConstruct>();
  		a=new EuclideanPoint(GeoConstruct.POINT,temp,w);
  		b=new EuclideanPoint(GeoConstruct.POINT,temp,z);
  		temp.add(a);	temp.add(b);
  		l=new EuclideanLine(GeoConstruct.LINE,temp);
  		l.update();	l.getNewXYZ(v1);	l.setXYZ(v1);	temp.clear();
  		v1[0]=t[0]+r;	v1[1]=t[1];		c=new EuclideanPoint(GeoConstruct.POINT,temp,v1);
  		v2[0]=t[0];		v2[1]=t[1]+r;	d=new EuclideanPoint(GeoConstruct.POINT,temp,v2);
  		temp.clear();	temp.add(c);	temp.add(d);
  		m=new EuclideanLine(GeoConstruct.LINE,temp);
  		temp.clear();	temp.add(l);	temp.add(m);
  		e = new EuclideanLINEintLINE(GeoConstruct.LINEintLINE0,temp,v1);
  		m.update();		e.update();		e.getNewXYZ(n[0]);
  		v1[0]=t[0]-r; 	c.setNewXYZ(v1);
  		v2[1]=t[1]-r;	d.setNewXYZ(v2);
  		m.update();		e.update();		e.getNewXYZ(n[1]);
  		if (bit) {for (int i=0;i<2;i++)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
  		}
  		else for (int i=1;i>-1;i--)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
	}
	else if (Math.abs(v[0]+v[1])<.00001) {//slope==-1
		EuclideanPoint a,b,c,d,e;
  		EuclideanLine l,m;
  		double[] v1={0,0,0},v2={0,0,0},z={w[0]+v[0],w[1]+v[1],0};
  		double[][] n={{0,0,0},{0,0,0}};
  		double r=Math.abs(u[0]-t[0])+Math.abs(u[1]-t[1]);
  		LinkedList<GeoConstruct> temp=new LinkedList<GeoConstruct>();
  		a=new EuclideanPoint(GeoConstruct.POINT,temp,w);
  		b=new EuclideanPoint(GeoConstruct.POINT,temp,z);
  		temp.add(a);	temp.add(b);
  		l=new EuclideanLine(GeoConstruct.LINE,temp);
  		l.update();	l.getNewXYZ(v1);	l.setXYZ(v1);	temp.clear();
  		v1[0]=t[0]-r;	v1[1]=t[1];		c=new EuclideanPoint(GeoConstruct.POINT,temp,v1);
  		v2[0]=t[0];		v2[1]=t[1]+r;	d=new EuclideanPoint(GeoConstruct.POINT,temp,v2);
  		temp.clear();	temp.add(c);	temp.add(d);
  		m=new EuclideanLine(GeoConstruct.LINE,temp);
  		temp.clear();	temp.add(l);	temp.add(m);
  		e = new EuclideanLINEintLINE(GeoConstruct.LINEintLINE0,temp,v1);
  		m.update();		e.update();		e.getNewXYZ(n[0]);
  		v1[0]=t[0]+r; 	c.setNewXYZ(v1);
  		v2[1]=t[1]-r;	d.setNewXYZ(v2);
  		m.update();		e.update();		e.getNewXYZ(n[1]);
  		if (bit) {for (int i=0;i<2;i++)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
  		}
  		else for (int i=1;i>-1;i--)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
	}
  	else {// slope of line not equal +/-1
  		EuclideanPoint a,b,c,d,e;
  		EuclideanLine l,m;
  		double[] v1={0,0,0},v2={0,0,0},z={w[0]+v[0],w[1]+v[1],0};
  		double[][] n={{0,0,0},{0,0,0},{0,0,0},{0,0,0}};
  		double r=Math.abs(u[0]-t[0])+Math.abs(u[1]-t[1]);
  		LinkedList<GeoConstruct> temp=new LinkedList<GeoConstruct>();
  		a=new EuclideanPoint(GeoConstruct.POINT,temp,w);
  		b=new EuclideanPoint(GeoConstruct.POINT,temp,z);
  		temp.add(a);	temp.add(b);
  		l=new EuclideanLine(GeoConstruct.LINE,temp);
  		l.update();	l.getNewXYZ(v1);	l.setXYZ(v1);	temp.clear();
  		v1[0]=t[0]+r;	v1[1]=t[1];		c=new EuclideanPoint(GeoConstruct.POINT,temp,v1);
  		v2[0]=t[0];		v2[1]=t[1]+r;	d=new EuclideanPoint(GeoConstruct.POINT,temp,v2);
  		temp.clear();	temp.add(c);	temp.add(d);
  		m=new EuclideanLine(GeoConstruct.LINE,temp);
  		temp.clear();	temp.add(l);	temp.add(m);
  		e = new EuclideanLINEintLINE(GeoConstruct.LINEintLINE0,temp,v1);
  		m.update();		e.update();		e.getNewXYZ(n[0]);
  		v1[0]=t[0]-r; 	c.setNewXYZ(v1);
  		m.update();		e.update();		e.getNewXYZ(n[1]);
  		v2[1]=t[1]-r;	d.setNewXYZ(v2);
  		m.update();		e.update();		e.getNewXYZ(n[2]);
  		v1[0]=t[0]+r;	c.setNewXYZ(v1);
  		m.update();		e.update();		e.getNewXYZ(n[3]);
  		if (bit) {for (int i=0;i<4;i++)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
  		}
  		else for (int i=3;i>-1;i--)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
  		
  	}
	if (MathEqns.norm(x)>100) return false;
	return true;
  }
  public static boolean calculateManCC(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
		//circ with cntr t & pt u intersects circle with cntr v and pt w in pt x
		x[0]=100;	x[1]=100;	x[2]=0;
		double  r=Math.abs(v[0]-w[0])+Math.abs(v[1]-w[1]),
				s=Math.abs(t[0]-u[0])+Math.abs(t[1]-u[1]);
		double[][] n={{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0}};
		double[] v1={0,0,0},v2={0,0,0},nm={0,0,0};
		EuclideanPoint a,b;
		EuclideanLine c;
		v1[0]=v[0]+r;	v1[1]=v[1];
		v2[0]=v[0];		v2[1]=v[1]+r;
		LinkedList<GeoConstruct> temp=new LinkedList<GeoConstruct>();
  		a=new EuclideanPoint(GeoConstruct.POINT,temp,v1);
		b=new EuclideanPoint(GeoConstruct.POINT,temp,v2);
		temp.add(a);	temp.add(b);
		c=new EuclideanLine(GeoConstruct.LINE,temp);
		c.update();	c.getNewXYZ(nm);
		calculateManCL(t,u,nm,v1,n[0],true);
		calculateManCL(t,u,nm,v1,n[1],false);
		v1[0]=v[0]-r;	a.setNewXYZ(v1);	c.update();	c.getNewXYZ(nm);
		calculateManCL(t,u,nm,v1,n[2],true);
		calculateManCL(t,u,nm,v1,n[3],false);
		v2[1]=v[1]-r;	b.setNewXYZ(v2);	c.update();	c.getNewXYZ(nm);
		calculateManCL(t,u,nm,v1,n[4],true);
		calculateManCL(t,u,nm,v1,n[5],false);
		v1[0]=v[0]+r;	a.setNewXYZ(v1);	c.update();	c.getNewXYZ(nm);
		calculateManCL(t,u,nm,v1,n[6],true);
		calculateManCL(t,u,nm,v1,n[7],false);
		if (bit) {for (int i=0;i<8;i++)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-s)<.0001  &&
  				Math.abs(Math.abs(n[i][0]-v[0])+Math.abs(n[i][1]-v[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
  		}
  		else for (int i=7;i>-1;i--)
  			if (Math.abs(Math.abs(n[i][0]-t[0])+Math.abs(n[i][1]-t[1])-s)<.0001  &&
  	  			Math.abs(Math.abs(n[i][0]-v[0])+Math.abs(n[i][1]-v[1])-r)<.0001) {
  				x[0]=n[i][0];	x[1]=n[i][1];
  			}
		
		if (MathEqns.norm(x)>100) return false;
		return true;
	  }  
}

