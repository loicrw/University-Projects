# Loïc's directory
setwd("C:/Users/Loic RW/Google Drive/Big Data and Business Analytics/Assignments/Assignment 3")

# Rutger's directory
#setwd("~/Documents/Erasmus/BDBA/R")

#----------------------
# Q1 & 2 See Markdown 
#---------------------- 


#----------------------
# Q3 TWITTER ASSIGNMENT
#---------------------- 

# Making sure date is set
today <-Sys.Date()

# Getting tweets using for Loop
  for (t in 1:5) {
    # Searching for tweets containing offetting in the last 5 days
    myTweets_search1 <- searchTwitter("offsetting", lang = "en",
                                      since = strftime(today-t, format ="%Y-%m-%d"),
                                      until = strftime(today-t+1, format = "%Y-%m-%d"))
    # Remove retweets and store in dataframe 
    dfTweets_search1 <- twListToDF(strip_retweets(myTweets_search1, strip_manual = TRUE, strip_mt = TRUE))
    
    # Store Results  
    # First run creates a data frame, all other runs are appended
    if (t == 1) {
      dfTweets_search1_all <- dfTweets_search1
    } else {
      dfTweets_search1_all <- rbind(dfTweets_search1_all, dfTweets_search1)}
  }
  
  for (t in 1:5) {
    # Searching for tweets containing aviation in the last 5 days
    myTweets_search2 <- searchTwitter("aviation", lang = "en",
                                      since = strftime(today-t, format ="%Y-%m-%d"),
                                      until = strftime(today-t+1, format = "%Y-%m-%d"))
    # Remove retweets and store in dataframe 
    dfTweets_search2 <- twListToDF(strip_retweets(myTweets_search2, strip_manual = TRUE, strip_mt = TRUE))
    
    # Store Results  
    # First run creates a data frame, all other runs are appended
    if (t == 1) {
      dfTweets_search2_all <- dfTweets_search2
    } else {
      dfTweets_search2_all <- rbind(dfTweets_search2_all, dfTweets_search2)}
  }
  
  for (t in 1:5) {
    # Searching for tweets containing nudging in the last 5 days
    myTweets_search3 <- searchTwitter("nudging OR nudge", lang = "en",
                                      since = strftime(today-t, format ="%Y-%m-%d"),
                                      until = strftime(today-t+1, format = "%Y-%m-%d"))
    # Remove retweets and store in dataframe 
    dfTweets_search3 <- twListToDF(strip_retweets(myTweets_search3, strip_manual = TRUE, strip_mt = TRUE))
    
    # Store Results  
    # First run creates a data frame, all other runs are appended
    if (t == 1) {
      dfTweets_search3_all <- dfTweets_search3
    } else {
      dfTweets_search3_all <- rbind(dfTweets_search3_all, dfTweets_search3)}
  }
  
# Combine data frames with all tweets
  dfTweets <- rbind(dfTweets_search1_all, dfTweets_search2_all, dfTweets_search3_all)

  
 # NOT FINISHED
  
  
  
  
  
  
#--------------------
# Q4 Spotify ASSIGNMENT
#--------------------

#Installing the necessary packages  
install.packages("class", dependencies = TRUE )
install.packages ("cluster", dependencies = TRUE )
library(cluster)
library(class)  
library(C50)
library(rpart)
library(rpart.plot)

#Loading Data & removing fist row (index variable)
spotify <-read.csv(file="spotify.csv", header = TRUE)
spotify <- spotify[2:17]

# Inspecting Data
str(spotify)
summary(spotify)     
colSums(is.na(spotify))

# Removing 68 NA instances
spotify <- spotify[complete.cases(spotify) ,]


# Scaling all numerical variables
spotify_raw <- spotify[,c(1:5 ,7,8,10,11,13)] 
spotify_scaled <- scale(spotify[,c(1:5,7,8,10,11,13)])
df_spotify_scaled <- as.data.frame(spotify_scaled)


#K-means cluster analysis


# Find cluster solution, K = 4
rsltKmeans <- kmeans(df_spotify_scaled, 4) 
# Cluster Plot against 1st 2 principal components
clusplot(df_spotify_scaled, rsltKmeans$cluster, 
         color=TRUE, shade=TRUE, 
         labels=1, lines=0)

# Find cluster solution, K = 2
rsltKmeans <- kmeans(df_spotify_scaled, 6) 
# Cluster Plot against 1st 2 principal components
clusplot(df_spotify_scaled, rsltKmeans$cluster, 
         color=TRUE, shade=TRUE, 
         labels=1, lines=0)
# Find cluster solution, K = 2
rsltKmeans <- kmeans(df_spotify_scaled,2) 
# Cluster Plot against 1st 2 principal components
clusplot(df_spotify_scaled, rsltKmeans$cluster, 
         color=TRUE, shade=TRUE, 
         labels=1, lines=0)

wss <- (nrow(df_spotify_scaled)-1)*sum(apply(df_spotify_scaled,2,var))
for (i in 2:15) wss[i] <- sum(kmeans(df_spotify_scaled,
                                     centers=i)$withinss)
plot(1:15, wss, type="b", xlab="Number of Clusters",
     ylab="Within groups sum of squares")

rsltKmeans <- kmeans(df_spotify_scaled, 2) 

# Clearly steepest decrease at 2 clusters

# Building an decision Tree

# Add the cluster values to the original data set (the target variable)
spotify <- data.frame(spotify, cluster=as.factor(rsltKmeans$cluster))

# A decision tree model to explain Cluster memberships
mdlTree <- cluster ~ loudness + instrumentalness + danceability + energy + liveness + loudness 

rsltTree <-rpart(mdlTree, 
                 data = spotify,
                 method = "class", 
                 parms=list(split="information"))

# Plot the decision tree
rpart.plot(rsltTree, 
           box.col= c("grey","green","grey")[rsltTree$frame$yval], 
           extra = 102)

