import java.time.Duration;


public class PerformanceResult {

	public int Depth;
    public long Nodes;
    public Duration TimeSpan;
    
   public PerformanceResult(PerformanceResult performanceResult) {
    	Depth = performanceResult.Depth;
    	Nodes = performanceResult.Nodes;
    	TimeSpan = performanceResult.TimeSpan;
    }
    
    public PerformanceResult() {
    	Depth = 0;
    	Nodes = 0;
    	TimeSpan = null;
    }
}
