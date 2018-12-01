require(twitteR)

setwd("C:/Users/Loic RW/Google Drive/Big Data and Business Analytics/Assignments/Assignment 3")

ck <- "EUENyJUa8hrF1UGX2hGtntsAt"
cs <- "UmARrjPzF2uTGOpzwmgoEvfDwT8kaIUjsiBr9ZLeV8bhfmWH5j"
at <- "384339592-ENp0huPiFt1kCAGZQzo44kH8C8aaGgEkhFTPz38x"
as <- "gncNiTOeBvz52QIGdubR1Z2pjSneIWcqAKXajiVEKLvw1"

setup_twitter_oauth(ck, cs, access_token = at, access_secret = as)

t_stream <- searchTwitter('Facebook', resultType="recent", n=500)

df <- do.call("rbind", lapply(t_stream, as.data.frame))

# lapply applies a function to every element of a list and returns a list as result - it transforms the
# structure into a list of data.frames (one for each tweet)
# do.call executs a function on an argument (the new tweet-list). The function is here rbind(), i.e.the elements
# are connected row-wise, creating a single data frame in the end

my_columns <- subset(df, select=c("text","created","screenName","retweetCount","isRetweet","id")) 
# we only need these columns for our analysis

my_columns[,1] <- gsub('"',"",my_columns[,1])
# before we can use the data frame in Access we need to delete all quotation marks from the text field

write.table(my_columns, "tweets.csv", row.names = FALSE, col.names = TRUE, sep =";")
# This saves the tweets as a csv-file that can be imported into Access.

my_columns <- read.csv("tweets.csv", sep=";")

#my_columns <- twListToDF(strip_retweets(my_columns, strip_manual = TRUE, strip_mt = TRUE))




#Create list that has the number of characters in each tweet as elements (research function "nchar"), 
#then add the list as a column to the data frame 

tweetlength = list(c(nchar((my_columns$text))))                                     #list created
my_columns$tweetlength = nchar((my_columns$text))                                   #list added as a column


# Create a word cloud of the contents of the tweets, save the word cloud as a png file)

require(tm)        #load both packages
require(wordcloud) #load both packages

my_columns$text = sapply(my_columns$text, function(row) iconv(row, "latin1", "ASCII", sub = "")) #remove emoji's
my_columns_text = c(my_columns$text)                                                           #create character vector
tweets_corpus = Corpus(VectorSource(my_columns_text))                                          #create corpus
tweets_corpus_clean = tm_map(tweets_corpus, removePunctuation)                                 #remove punctuation
tweets_corpus_clean = tm_map(tweets_corpus_clean, content_transformer(tolower))                #only lower case
tweets_corpus_clean = tm_map(tweets_corpus_clean, removeWords, stopwords("english"))           #remove english stopwords (although we have multiple languages)
tweets_corpus_clean = tm_map(tweets_corpus_clean, removeWords, stopwords("french"))

tweets_corpus_clean = tm_map(tweets_corpus_clean, removeNumbers)                               #remove numbers
tweets_corpus_clean = tm_map(tweets_corpus_clean, stripWhitespace)                             #remove unnecessary white space
tweets_corpus_clean = tm_map(tweets_corpus_clean, removeWords, c("facebook", "https"))             #remove obvious words


wordcloud(tweets_corpus_clean, random.order = F, max.words = 100, scale = c(3, 0.5), colors = rainbow(50)) #create wordcloud







