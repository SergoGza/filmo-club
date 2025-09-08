package com.videoclub.filmoapp.batch.exporter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilmExportJobRunner implements CommandLineRunner {

  private final JobLauncher jobLauncher;
  private final Job filmExportJob;

  @Override
  public void run(String... args) throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
            .addLong("jobId", System.currentTimeMillis())
            .toJobParameters();

    jobLauncher.run(filmExportJob, jobParameters);
  }
}


