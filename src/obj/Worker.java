package obj;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import app.Main;
import storage.TreeNode;
import utils.Log;

public class Worker implements Runnable {

	private CyclicBarrier barrier;
	private List<TreeNode> treeNodes;
	
	public Worker(CyclicBarrier barrier, List<TreeNode> treeNodes) {
		this.barrier = barrier;
		this.treeNodes = treeNodes;
	}
	
	@Override
	public void run() {
		long startTime = System.nanoTime();
		for (TreeNode treeNode: this.treeNodes) {
			Main.mapObjects.addAll(treeNode.getMapObjects());
		}
		Log.debug("Thread " + Thread.currentThread().getName() + ": Loading " + treeNodes.size() + " nodes took " + ((System.nanoTime() - startTime) / 1000000000) + " seconds.");
		try {
			barrier.await();
		} 
		catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	

	
}
