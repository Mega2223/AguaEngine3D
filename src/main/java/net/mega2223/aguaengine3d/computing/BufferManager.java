package net.mega2223.aguaengine3d.computing;

import java.util.ArrayList;
import java.util.List;

public class BufferManager {
    //reintroducing the concept of memory leaks for Java :)
    private BufferManager(){}

    public static final BufferManager manager = new BufferManager();

    private final List<ThreadBufferManager> threadManagers = new ArrayList<>(20);

    private ThreadBufferManager getManagerForThread(long threadID){
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

    public float[] allocateVec4(){
        long threadID = Thread.currentThread().getId();
        ThreadBufferManager manager = getManagerForThread(threadID);
        return manager.allocateBuffer4();
    }

    public float[] allocateMatrix4(){
        long threadID = Thread.currentThread().getId();
        ThreadBufferManager manager = getManagerForThread(threadID);
        return manager.allocateBuffer16();
    }

    public void freeVec4(float[] buffer){
        long threadID = Thread.currentThread().getId();
        ThreadBufferManager manager = getManagerForThread(threadID);
        manager.deallocateBuffer4(buffer);
    }

    public void freeMat4(float[] buffer){
        long threadID = Thread.currentThread().getId();
        ThreadBufferManager manager = getManagerForThread(threadID);
        manager.deallocateBuffer16(buffer);
    }

    private static class ThreadBufferManager{
        public final long threadID;

        public final List<ObjBuffer<float[]>> float4Buffers = new ArrayList<>(20);
        public final List<ObjBuffer<float[]>> float16Buffers = new ArrayList<>(20);

        public ThreadBufferManager(long threadID){
            this.threadID = threadID;
        }

        public float[] allocateBuffer4(){
            for (int i = 0; i < float4Buffers.size(); i++) {
                ObjBuffer<float[]> act = float4Buffers.get(i);
                if(!act.allocated){
                    act.allocated = true;
                    return act.buffer;
                }
            }
            ObjBuffer<float[]> ret = new ObjBuffer<>(new float[4]);
            ret.allocated = true;
            return ret.buffer;
        }

        public void deallocateBuffer4(float[] buffer){
            for (int i = 0; i < float4Buffers.size(); i++) {
                ObjBuffer<float[]> act = float4Buffers.get(i);
                if(act.allocated && act.buffer == buffer){
                    act.allocated = false;
                }
            }
            throw new UnsupportedOperationException("Buffer is not currently allocated");
        }

        public float[] allocateBuffer16(){
            for (int i = 0; i < float16Buffers.size(); i++) {
                ObjBuffer<float[]> act = float16Buffers.get(i);
                if(!act.allocated){
                    act.allocated = true;
                    return act.buffer;
                }
            }
            ObjBuffer<float[]> ret = new ObjBuffer<>(new float[16]);
            ret.allocated = true;
            return ret.buffer;
        }

        public void deallocateBuffer16(float[] buffer){
            for (int i = 0; i < float16Buffers.size(); i++) {
                ObjBuffer<float[]> act = float16Buffers.get(i);
                if(act.allocated && act.buffer == buffer){
                    act.allocated = false;
                }
            }
            throw new UnsupportedOperationException("Buffer is not currently allocated");
        }
    }

    private static class ObjBuffer<B>{
        public boolean allocated = false;
        public final B buffer;

        ObjBuffer(B buffer){
            this.buffer = buffer;
        }
    }
}
