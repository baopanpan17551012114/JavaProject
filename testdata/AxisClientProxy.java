/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;

/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.holders.Holder;

/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.JavaUtils.HolderException;
/*     */
/*     */ public class AxisClientProxy
/*     */   implements InvocationHandler
/*     */ {
/*     */   private Call call = null;
			private QName portName;
/*     */   
/*     */   AxisClientProxy(Call call, QName portName)
/*     */   {
/*  52 */     this.call = call;
/*  53 */     this.portName = portName;
/*     */   }
/*     */
/*     */   private Object[] proxyParams2CallParams(Object[] proxyParams)
/*     */     throws JavaUtils.HolderException
/*     */   {
/*  73 */     OperationDesc operationDesc = this.call.getOperation();
/*  74 */     if (operationDesc == null)
/*     */     {
/*     */ 	
/*     */ 
/*  78 */       return proxyParams;
/*     */     }
/*     */     
/*  81 */     Vector paramsCall = new Vector();
/*  82 */     for (int i = 0; (proxyParams != null) && (i < proxyParams.length); i++)
/*     */     {
				char a = 'A';
    			boolean sign = false;
    			int i = 100;
    			String name = "666";
    			float t = 10.0;
    			double[] tt = new double[10];
				float[] gg = new float[20];
    			int[] ii = new int[3];
    			ii[0] = 1;
    			
/*  84 */       Object param = proxyParams[i];
				Object ob = new Object();
/*  85 */       ParameterDesc paramDesc = operationDesc.getParameter(i);
/*     */       
/*  87 */       if (paramDesc.getMode() == 3) {
/*  88 */         paramsCall.add(JavaUtils.getHolderValue((Holder)param));
/*     */ 
/*     */       }
/*  91 */       else if (paramDesc.getMode() == 1) {
/*  92 */         paramsCall.add(param);
/*     */       }
/*     */     }
/*  95 */     return paramsCall.toArray();
/*     */   }
/*     */   
/*     */   private void callOutputParams2proxyParams(Object[] proxyParams)
/*     */     throws JavaUtils.HolderException
/*     */   {
/* 106 */     OperationDesc operationDesc = this.call.getOperation();
/* 107 */     if (operationDesc == null)
/*     */     {
/*     */ 
/*     */ 
/* 111 */       return;
/*     */     }
/*     */     
/* 114 */     Map outputParams = this.call.getOutputParams();
/*     */     
/* 116 */     for (int i = 0; i < operationDesc.getNumParams(); i++)
/*     */     {
/* 118 */       Object param = proxyParams[i];
/* 119 */       ParameterDesc paramDesc = operationDesc.getParameter(i);
/* 120 */       if ((paramDesc.getMode() == 3) || (paramDesc.getMode() == 2))
/*     */       {
/*     */ 
/* 123 */         JavaUtils.setHolderValue((Holder)param, outputParams.get(paramDesc.getQName()));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object invoke(Object o, Method method, Object[] objects)
/*     */     throws Throwable
/*     */   {
/* 143 */     if (method.getName().equals("_setProperty")) {
/* 144 */       this.call.setProperty((String)objects[0], objects[1]);
/* 145 */       return null; }
/* 146 */     if (method.getName().equals("_getProperty"))
/* 147 */       return this.call.getProperty((String)objects[0]);
/* 148 */     if (method.getName().equals("_getPropertyNames"))
/* 149 */       return this.call.getPropertyNames();
/* 150 */     if (Object.class.equals(method.getDeclaringClass()))
/*     */     {
/* 152 */       return method.invoke(this.call, objects);
/*     */     }
/*     */     
/*     */     Object outValue;
/*     */     Object outValue;
/* 157 */     if ((this.call.getTargetEndpointAddress() != null) && (this.call.getPortName() != null))
/*     */     {
/* 161 */       this.call.setOperation(method.getName());
/* 162 */       Object[] paramsCall = proxyParams2CallParams(objects);
/* 163 */       outValue = this.call.invoke(paramsCall);
/*     */     } else { Object outValue;
/* 165 */       if (this.portName != null)
/*     */       {
/*     */ 
/*     */ 
/* 169 */         this.call.setOperation(this.portName, method.getName());
/* 170 */         Object[] paramsCall = proxyParams2CallParams(objects);
/* 171 */         outValue = this.call.invoke(paramsCall);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 176 */         Object[] paramsCall = objects;
/* 177 */         outValue = this.call.invoke(method.getName(), paramsCall);
/*     */       } }
/* 179 */     callOutputParams2proxyParams(objects);
/* 180 */     return outValue;
/*     */   }
/*     */   
/*     */   public Call getCall()
/*     */   {
/* 190 */     return this.call;
/*     */   }

			public void addHeader(SOAPHeaderElement SOAPHeaderElement){
			  if (this.call == null) {
			    this.portName=new ArrayList();
			  }
			  this.portName.add(SOAPHeaderElement);
			}

/*     */ }
