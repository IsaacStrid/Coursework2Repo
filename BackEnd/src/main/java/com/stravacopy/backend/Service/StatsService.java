package com.stravacopy.backend.Service;

import com.stravacopy.backend.Model.Split;
import com.stravacopy.backend.Model.Workout;
import com.stravacopy.backend.Model.RunningStats;
import com.stravacopy.backend.Model.LeaderboardEntry;
import com.stravacopy.backend.Model.User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class StatsService {

    public RunningStats generateWorkOutStats(Workout workout) {
        List<Split> splits = workout.getSplits();

        long totalDistance = 0;
        int totalHeartRate = 0;
        int totalSpeed = 0;
        int count = 0;
        double fastest1kTime = 0;
        double fastest5kTime = 0;
        double fastest10kTime = 0;
        double highestSpeed = 0;
        int highestHeartRate = 0;

        for (Split split : splits) {

            totalDistance += split.getDistance();
            totalHeartRate += split.getHeartRate();
            totalSpeed += split.getSpeed();

            if (highestSpeed > split.getSpeed()){
                highestSpeed = split.getSpeed();
            }
            if (highestHeartRate > split.getHeartRate()){
                highestHeartRate = split.getHeartRate();
            }

            count++;

        }

        double avgHeartRate = count > 0 ? (double) totalHeartRate / count : 0;
        double avgSpeed = count > 0 ? (double) totalSpeed / count : 0;

        fastest1kTime = GenerateFastestSegments(splits, 1000);
        fastest5kTime = GenerateFastestSegments(splits, 5000);
        fastest10kTime = GenerateFastestSegments(splits, 10000);

        // mood stats...

        return new RunningStats(totalDistance, avgHeartRate, avgSpeed, fastest1kTime,fastest5kTime,fastest10kTime, highestSpeed, highestHeartRate, 0);
    }

    public RunningStats generateUserStats(List<Workout> workouts){

        long totalDistance = 0;
        double totalHeartRate = 0;
        double totalSpeed = 0;
        int count = 0;

        double fastest1kTime = 0;
        double fastest5kTime = 0;
        double fastest10kTime = 0;
        double longestDistance = 0;
        double highestSpeed = 0;
        int highestHeartRate = 0;

        for (Workout workout: workouts) {

            RunningStats workoutStats = workout.getRunningStats();

            long workoutDistance = workoutStats.getTotalDistance();
            double workoutAvgSpeed = workoutStats.getAvgSpeed();
            double workoutMaxSpeed = workoutStats.getHighestSpeed();
            int workoutHighestHeartRate = workoutStats.getHighestHeartRate();
            double workout1kTime = workoutStats.getFastest1kPace();
            double workout5kTime = workoutStats.getFastest5kPace();
            double workout10kTime = workoutStats.getFastest10kPace();

            totalDistance += workoutDistance;
            totalHeartRate += workoutStats.getAvgHeartRate();
            totalSpeed += workoutAvgSpeed;

            if (workoutDistance > longestDistance){
                longestDistance = workoutDistance;
            }
            if (workoutMaxSpeed > highestSpeed){
                highestSpeed = workoutMaxSpeed;
            }
            if (highestHeartRate > workoutHighestHeartRate){
                highestHeartRate = workoutHighestHeartRate;
            }
            if (workout1kTime > fastest1kTime){
                fastest1kTime = workout1kTime;
            }
            if (workout5kTime > fastest5kTime){
                fastest5kTime = workout5kTime;
            }
            if (workout10kTime > fastest10kTime){
                fastest10kTime = workout10kTime;
            }

            count++;
        }

        double avgHeartRate = count > 0 ? totalHeartRate / count : 0;
        double avgSpeed = count > 0 ? totalSpeed / count : 0;

        return new RunningStats(totalDistance, avgHeartRate, avgSpeed, fastest1kTime, fastest5kTime, fastest10kTime, highestSpeed, highestHeartRate, longestDistance);
    }

    public double GenerateFastestSegments(List<Split> splits, long distance){
        Duration fastestTime = null;

        for (int start = 0; start < splits.size(); start++) {
            long totalDistance = 0;
            LocalDateTime startTime = splits.get(start).getTimeStamp();

            for (int end = start; end < splits.size(); end++) {
                totalDistance += splits.get(end).getDistance();

                if (totalDistance >= distance) {
                    LocalDateTime endTime = splits.get(end).getTimeStamp();
                    Duration segmentTime = Duration.between(startTime, endTime);

                    if (fastestTime == null || segmentTime.compareTo(fastestTime) < 0) {
                        fastestTime = segmentTime;
                    }

                    break;
                }
            }
        }

        assert fastestTime != null;
        return fastestTime.toMinutes();

    }
    public List<LeaderboardEntry> getLeaderboardByType(String type, List<User> users) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (User user : users) {
            RunningStats stats = user.getUserStatistics();
            double value = switch (type.toLowerCase()) {
                case "fastest5k" -> stats.getFastest5kPace();
                case "fastest1k" -> stats.getFastest1kPace();
                case "fastest10k" -> stats.getFastest10kPace();
                case "totaldistance" -> stats.getTotalDistance();
                case "topspeed" -> stats.getHighestSpeed();
                default -> -1;
            };

            if (value > 0) {
                leaderboard.add(new LeaderboardEntry(user.getName(), value));
            }
        }

        // Sorting: fastest times = ascending
        // everything else = descending
        if (type.toLowerCase().startsWith("fastest")) {
            leaderboard.sort(Comparator.comparing(LeaderboardEntry::getValue));
        } else {
            leaderboard.sort(Comparator.comparing(LeaderboardEntry::getValue).reversed());
        }

        return leaderboard;
    }
}
