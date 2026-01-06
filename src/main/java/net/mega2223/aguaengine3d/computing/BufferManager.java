package net.mega2223.aguaengine3d.computing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BufferManager {
    //reintroducing the concept of memory leaks for Java :)
    private BufferManager(){}

    public static int DEBUG = 0;

    private static final List<ThreadBufferManager> threadManagers = new ArrayList<>(20);

    private static ThreadBufferManager getManagerForThread(long threadID){
        synchronized (threadManagers){
            for (ThreadBufferManager act : threadManagers){
                if(act.threadID == threadID){
                    return act;
                }
            }
            ThreadBufferManager manager = new ThreadBufferManager(threadID);
            threadManagers.add(manager);
            return manager;
        }
    }

    public static float[] allocateVec4(){
        long threadID = Thread.currentThread().getId();
        ThreadBufferManager manager = getManagerForThread(threadID);
        return manager.allocateBuffer4();
    }

    public static float[] allocatePermanentVec4(float v0, float v1, float v2, float v3){
        return new float[]{v0,v1,v2,v3}; // TODO KKKKKKKKKKKKKK
    }

//    public static float[] allocateMatrix4(){
//        long threadID = Thread.currentThread().getId();
//        ThreadBufferManager manager = getManagerForThread(threadID);
//        return manager.allocateBuffer16();
//    }

    public static void freeVec4(float[] buffer){
        long threadID = Thread.currentThread().getId();
        ThreadBufferManager manager = getManagerForThread(threadID);
        manager.deallocateBuffer4(buffer);
    }

//    public static void freeMat4(float[] buffer){
//        long threadID = Thread.currentThread().getId();
//        ThreadBufferManager manager = getManagerForThread(threadID);
//        manager.deallocateBuffer16(buffer);
//    }

    public static int getAllocatedVec4Count(){
        int count = 0;
        for(ThreadBufferManager act : threadManagers){
            count += act.float4Buffers.size();
        }
        return count;
    }

    private static class ThreadBufferManager{
        public final long threadID;

        public final List<ObjBuffer<float[]>> float4Buffers = new ArrayList<>(20);
        public final List<ObjBuffer<float[]>> float16Buffers = new ArrayList<>(20);

        public ThreadBufferManager(long threadID){
            this.threadID = threadID;
        }

        public float[] allocateBuffer4(){
            ObjBuffer<float[]> buffer = null;
            for (int i = 0; i < float4Buffers.size(); i++) {
                ObjBuffer<float[]> act = float4Buffers.get(i);
                if(!act.allocated){
                    buffer = act;
                    break;
                }
            }
            if(buffer == null){
                buffer = new ObjBuffer<>(new float[4]);
                float4Buffers.add(buffer);
            }
            if(DEBUG > 0){
                StackTraceElement[] trace = Thread.currentThread().getStackTrace();
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < trace.length; i++) {
                    b.append(trace[i].getClassName()).append(":")
                            .append(trace[i].getMethodName())
                            .append("[")
                            .append(trace[i].getLineNumber())
                            .append("]")
                            .append(" ");
                }
                buffer.owner = b.toString();
            }

            Arrays.fill(buffer.buffer,0);
            buffer.allocated = true;
            buffer.lastAllocation = System.currentTimeMillis();
            return buffer.buffer;
        }

        public void deallocateBuffer4(float[] buffer){
            for (int i = 0; i < float4Buffers.size(); i++) {
                ObjBuffer<float[]> act = float4Buffers.get(i);
                if(act.allocated && act.buffer == buffer){
                    act.allocated = false;
                    return;
                }
            }
            throw new UnsupportedOperationException("Buffer is not currently allocated");
        }

//        public float[] allocateBuffer16(){
//            for (int i = 0; i < float16Buffers.size(); i++) {
//                ObjBuffer<float[]> act = float16Buffers.get(i);
//                if(!act.allocated){
//                    act.allocated = true;
//                    act.lastAllocation = System.currentTimeMillis();
//                    return act.buffer;
//                }
//            }
//            ObjBuffer<float[]> ret = new ObjBuffer<>(new float[16]);
//            ret.allocated = true;
//            ret.lastAllocation = System.currentTimeMillis();
//            float16Buffers.add(ret);
//            return ret.buffer;
//        }
//
//        public void deallocateBuffer16(float[] buffer){
//            for (int i = 0; i < float16Buffers.size(); i++) {
//                ObjBuffer<float[]> act = float16Buffers.get(i);
//                if(act.allocated && act.buffer == buffer){
//                    act.allocated = false;
//                    return;
//                }
//            }
//            throw new UnsupportedOperationException("Buffer is not currently allocated");
//        }
    }

    private static class ObjBuffer<B>{
        public boolean allocated = false;
        long lastAllocation = -1;
        public String owner = null;
        public final B buffer;

        ObjBuffer(B buffer){
            this.buffer = buffer;
        }
    }

    //TODO allocatePermanent?
}
