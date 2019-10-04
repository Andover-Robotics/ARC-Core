[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0ed9efc1a65747c9ad806a66419d4054)](https://www.codacy.com/app/michael_47/ARC-Core?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Andover-Robotics/ARC-Core&amp;utm_campaign=Badge_Grade)

## Welcome!
This is the common code base for the Andover Robotics FTC teams. The code base of all three teams will include this library as a Git submodule.

Why do we designate it as a submodule? We would like to introduce new features and fix bugs during the season, and this choice helps teams receive ARC-Core updates more easily.

## Features
* Tank Drive and Mecanum Drivetrain implementations with encoder support
* Support for reading files from storage as configuration
* PID controller implementation
* Task System support using BeanShell
* Optimizing hardware wrapper classes like `CachedMotor`

## Setting up your Team's Repository
_This section is intended for the programming leaders of each team._
1. Perform `git init` locally to initialize an empty repository
2. Perform `git remote add upstream https://github.com/OpenFTC/OpenRC-Turbo.git` in order to set OpenRC Turbo as the upstream remote
3. Perform `git pull upstream master --allow-unrelated-histories -f` in order to pull OpenRC's source code into your local repository
4. Perform `git submodule add https://github.com/Andover-Robotics/ARC-Core`
5. Set the `origin` remote to your team's repository by performing `git remote add origin <origin url in either https or ssh>`
6. Add "ARC-Core" to the Android project by adding `, ':ARC-Core'` prior to `':TeamCode'` in `settings.gradle`
7. Set the `minSdkVersion` on line 41 of `build.common.gradle` from 19 to 24
8. Commit your changes
9. Push the local copy to the `origin` remote by performing `git push -u origin master`

After your repository is ready, make sure to copy the required resources onto your team's Robot Controller phone:

1. > Copy `libVuforia.so` from the `doc` folder of \[your\] repo into the `FIRST` folder on the RC's internal storage
2. > Because EasyOpenCv depends on [OpenCV-Repackaged](https://github.com/OpenFTC/OpenCV-Repackaged), you will also need to copy [`libOpenCvNative.so`](https://github.com/OpenFTC/OpenCV-Repackaged/blob/master/doc/libOpenCvNative.so) from the `/doc` folder of that repo into the `FIRST` folder on the internal storage of the Robot Controller
3. > Copy all of the files found in the `filesForDynamicLoad` folder of \[your\] repo into the FIRST folder on the RC's internal storage

(For reference, the FIRST folder's full path is `/storage/self/primary/FIRST`.)

## Getting Help

If you wish to receive clarification or assistance in troubleshooting a problem, please check [the ARC-Core JavaDocs](https://Andover-Robotics.github.io/ARC-Core) and [ARC Software](https://andover-robotics.gitbook.io/arc-software/).

The Chief Software Officers of the 2019-2020 season, Michael Peng and Daniel Ivanovich, are eager to help you achieve your best. Feel free to approach them with questions.


You should also join the [FTC Discord](https://discord.gg/first-tech-challenge), where you can receive help from some of the best teams in the world.

