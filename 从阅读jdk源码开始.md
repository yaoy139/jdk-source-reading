#读jdk源码的一些认识

  很多java开发的小伙伴都会阅读jdk源码，然而确不知道应该从哪读起。以下为小编整理的通常所需阅读的源码范围。 
标题为包名，后面序号为优先级1-4，优先级递减 
1、java.lang
优先级1：
  1)  Object 					        
  2)  String					        
  3)  AbstractStringBuilder 	
  4)  StringBuffer 			      
  5)  StringBuilder 			    
优先级2：
  6)  Boolean 				        
  7)  Byte 					          
  8)  Double 					        
  9)  Float 					        
  10) Integer 					      
  11) Long 					          
  12) Short 					        
  13) Thread 					        
  14) ThreadLocal 			      
优先级3：
  15) Enum 					          
  16) Throwable 				      
  17) Error 					        
  18) Exception 				      
 优先级4：
  19) Class 					        
  20) ClassLoader 			      
  21) Compiler				        
  22) System 					        
  23) Package 				        
  24) Void 					          

2、java.util
优先级1：
  1)  AbstractList 				    
  2)  AbstractMap 			      
  3)  AbstractSet 				    
  4)  ArrayList 				      
  5)  LinkedList 				      
  6)  HashMap 				        
  7)  Hashtable 				      
  8)  HashSet 				        
  9)  LinkedHashMap 			    
  10) LinkedHashSet 			    
  11) TreeMap 				        
  12) TreeSet 				        
优先级2：
  13) Vector					        
  14) Queue 					        
  15) Stack 					        
  16) SortedMap 				      
  17) SortedSet 				      
优先级3：
  18) Collections 				    
  19) Arrays 					        
  20) Comparator 			        
  21) Iterator 				        
优先级4：
  22) Base64 					        
  23) Date 					          
  24) EventListener 			    
  25) Random 				          
  26) SubList 					      
  27) Timer 					        
  28) UUID 					          
  29) WeakHashMap 			      

3、java.util.concurrent
优先级1：
  1)  ConcurrentHashMap 	    
优先级2：
  2)  Executor 				        
  3)  AbstractExecutorService 
  4)  ExecutorService 			  	
  5)  ThreadPoolExecutor 		  
  6)  BlockingQueue	 		      
  7)  AbstractQueuedSynchronizer 
  8)  CountDownLatch 		      
  9)  FutureTask 				      
  10) Semaphore 				      
  11) CyclicBarrier 			    
优先级3：
  13) CopyOnWriteArrayList 	  
  14) SynchronousQueue 		    
  15) BlockingDeque 			    
优先级4：
  16) Callable 				        

4、java.util.concurrent.atomic
优先级2：
  1)  AtomicBoolean 			    
  2)  AtomicInteger 			    
  3)  AtomicLong 			        
优先级3：  
  4)  AtomicReference 		    

5、java.lang.reflect
优先级2：
  1)  Field 					        
  2)  Method 				          
  
6、java.lang.annotation
优先级3：
  1)  Annotation 				      
  2)  Target 					        
  3)  Inherited 				      
  4)  Retention 				      
优先级4:
  5)  Documented 			        
  6)  ElementType 			      
  7)  Native 					        
  8)  Repeatable 				      
  
7、java.util.concurrent.locks
优先级2：
  1)  Lock 					          
  2)  Condition 				      
  3)  ReentrantLock 			    
  4)  ReentrantReadWriteLock  
  
8、java.io
优先级3：
  1)  File 					          
  2)  InputStream   			    
  3)  OutputStream  			    
优先级4：
  4)  Reader  				        
  5)  Writer  				        

9、java.nio
优先级3：
  1)  Buffer 					        
优先级4：
  2)  ByteBuffer 				      
  3)  CharBuffer 				      
  4)  DoubleBuffer 			      
  5)  FloatBuffer 				    
  6)  IntBuffer 				      
  7)  LongBuffer 				      
  8)  ShortBuffer 				    
  
10、java.sql
优先级3：
  1)  Connection 				      
  2)  Driver 					        
  3)  DriverManager 			    
  4)  JDBCType 				        
优先级4：
  5)  ResultSet 				      
  6)  Statement 				      
  
11、java.net
优先级3：
  1)  Socket 					        
  2)  ServerSocket 			      
优先级4：
  3)  URI 					          
  4)  URL			 			          
  5)  URLEncoder 			        
