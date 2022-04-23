package de.dagere.kopeme.kopemedata;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class VMResult {
   private double value;
   private double deviation;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Double min = null;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Double max = null;

   private long warmup;
   private long iterations;
   private long repetitions;

   // Was version in XML-format; is now commit
   private String commit;

   private ResultConfiguration vmRunConfiguration = new ResultConfiguration();
   // Should persist input order, therefore sorted map
   private LinkedHashMap<String, String> parameters;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private String javaVersion;
   private String cpu;
   private String memory;
   private long date;
   
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Long cpuTemperature;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean failure = false;
   
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean error = false;
   
   @JsonInclude(JsonInclude.Include.NON_NULL)
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

   public Double getMin() {
      return min;
   }

   public void setMin(Double min) {
      this.min = min;
   }

   public Double getMax() {
      return max;
   }

   public void setMax(Double max) {
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
   
   @JsonIgnore
   public Entry<String, String> getFirstParameter(){
      return parameters.entrySet().iterator().next();
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

   public Long getCpuTemperature() {
      return cpuTemperature;
   }

   public void setCpuTemperature(Long cpuTemperature) {
      this.cpuTemperature = cpuTemperature;
   }

   public Boolean isFailure() {
      return failure;
   }

   public void setFailure(Boolean failure) {
      this.failure = failure;
   }

   public Boolean isError() {
      return error;
   }

   public void setError(Boolean error) {
      this.error = error;
   }

   public Fulldata getFulldata() {
      return fulldata;
   }

   public void setFulldata(Fulldata fulldata) {
      this.fulldata = fulldata;
   }
}