package de.dagere.kopeme.kopemedata;

import java.util.LinkedHashMap;

public class VMResult {
   private double value;
   private double deviation;

   private double min;
   private double max;

   private long warmup;
   private long iterations;
   private long repetitions;

   // Was version in XML-format; is now commit
   private String commit;

   private ResultConfiguration vmRunConfiguration = new ResultConfiguration();
   // Should persist input order, therefore sorted map
   private LinkedHashMap<String, String> parameters;

   private String javaVersion;
   private String cpu;
   private String memory;
   private long date;
   private long cpuTemperature;

   private boolean failure;
   private boolean error;
   
   private Fulldata fulldata;

   public double getValue() {
      return value;
   }

   public void setValue(double value) {
      this.value = value;
   }

   public double getDeviation() {
      return deviation;
   }

   public void setDeviation(double deviation) {
      this.deviation = deviation;
   }

   public double getMin() {
      return min;
   }

   public void setMin(double min) {
      this.min = min;
   }

   public double getMax() {
      return max;
   }

   public void setMax(double max) {
      this.max = max;
   }

   public long getWarmup() {
      return warmup;
   }

   public void setWarmup(long warmup) {
      this.warmup = warmup;
   }

   public long getIterations() {
      return iterations;
   }

   public void setIterations(long iterations) {
      this.iterations = iterations;
   }

   public long getRepetitions() {
      return repetitions;
   }

   public void setRepetitions(long repetitions) {
      this.repetitions = repetitions;
   }

   public String getCommit() {
      return commit;
   }

   public void setCommit(String commit) {
      this.commit = commit;
   }

   public ResultConfiguration getVmRunConfiguration() {
      return vmRunConfiguration;
   }

   public void setVmRunConfiguration(ResultConfiguration vmRunConfiguration) {
      this.vmRunConfiguration = vmRunConfiguration;
   }

   public LinkedHashMap<String, String> getParameters() {
      return parameters;
   }

   public void setParameters(LinkedHashMap<String, String> parameters) {
      this.parameters = parameters;
   }

   public String getJavaVersion() {
      return javaVersion;
   }

   public void setJavaVersion(String javaVersion) {
      this.javaVersion = javaVersion;
   }

   public String getCpu() {
      return cpu;
   }

   public void setCpu(String cpu) {
      this.cpu = cpu;
   }

   public String getMemory() {
      return memory;
   }

   public void setMemory(String memory) {
      this.memory = memory;
   }

   public long getDate() {
      return date;
   }

   public void setDate(long date) {
      this.date = date;
   }

   public long getCpuTemperature() {
      return cpuTemperature;
   }

   public void setCpuTemperature(long cpuTemperature) {
      this.cpuTemperature = cpuTemperature;
   }

   public boolean isFailure() {
      return failure;
   }

   public void setFailure(boolean failure) {
      this.failure = failure;
   }

   public boolean isError() {
      return error;
   }

   public void setError(boolean error) {
      this.error = error;
   }

   public Fulldata getFulldata() {
      return fulldata;
   }

   public void setFulldata(Fulldata fulldata) {
      this.fulldata = fulldata;
   }
}