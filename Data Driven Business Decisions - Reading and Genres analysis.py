import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import pytz
from sklearn.cluster import KMeans
from datetime import datetime

reads = pd.read_csv('reads.csv')
reads['SERVER_EVENT_TIME'] = reads['SERVER_EVENT_TIME']*1000000
reads['SERVER_EVENT_TIME'] = pd.to_datetime(reads['SERVER_EVENT_TIME'])
reads['SERVER_EVENT_TIME'] = reads['SERVER_EVENT_TIME'].dt.tz_localize('Etc/GMT-0')
reads['SERVER_EVENT_TIME'] = reads['SERVER_EVENT_TIME'].dt.tz_convert('Etc/GMT+3')

print(max(reads['SERVER_EVENT_TIME']))

reads['date'] = reads['SERVER_EVENT_TIME'].dt.date
reads['date'] = pd.to_datetime(reads['date'], errors='coerce')
reads['day_of_week'] = reads['date'].dt.weekday_name
print(reads)

# -----------------------------------------------------------------------
# -----------------------------------------------------------------------
# story count

### Reads count distribution: many reading ID reads 1 story at a time
reads_hist = reads[reads['COUNT'] > 0]
reads_hist = reads_hist[reads_hist['COUNT'] < 101]

# plot histogram
# reads_hist["COUNT"].apply(np.log).hist()

plt.hist(reads_hist['COUNT'], bins=10, log = True)
plt.ylabel('Number of Reads O (log-scale)')
plt.xlabel('Number of Chapters every Read Occurence')
plt.title('Histogram Number of Chapters every Read Occurence')
plt.show()

print(100 * reads[reads['COUNT'] >100].count()['COUNT']/reads.count()['COUNT'])

# print(reads['COUNT'].max())

# testingread = reads[['ACCOUNT_ID', 'COUNT']]
# print(testingread.sort_values(by = 'COUNT', ascending = False))


# filtering ridiculous numbers of chapters read
reads = reads[reads['COUNT'] > 0]
reads = reads[reads['COUNT'] < 101]
print(100 * reads[reads['COUNT'] == 2 ].count()['COUNT']/reads.count()['COUNT'])



# deleting anonymous account id
reads = reads[reads['ACCOUNT_ID'] != 74747]
reads.count()


# -----------------------------------------------------------------------
# -----------------------------------------------------------------------
# stories set up
story = pd.read_csv('stories.csv')

# select only stories published
story = story[story['IS_PUBLISHED'] == 1]
# counting how many stories are published
print(story[story['IS_PUBLISHED'] == 0].count()['IS_PUBLISHED'])
# number of stories: 12615

print(story.count())

# convert NAN to 0
story['CATEGORY_REF'] = story['CATEGORY_REF'].fillna(0)
story['EXTRA_CATEGORY_REF'] = story['EXTRA_CATEGORY_REF'].fillna(0)

# convert float to integer
story['CATEGORY_REF'] = round(story['CATEGORY_REF']).astype(int)
story['EXTRA_CATEGORY_REF'] = round(story['EXTRA_CATEGORY_REF']).astype(int)

story.loc[(story.EXTRA_CATEGORY_REF == story.CATEGORY_REF), ['EXTRA_CATEGORY_REF']] = 0

story.loc[(story.EXTRA_CATEGORY_REF == 0) & (story.CATEGORY_REF == 0),
          ['CATEGORY_REF']] = 5


'''
story[(story.EXTRA_CATEGORY_REF == 5) & (story.CATEGORY_REF == 0)] # 0
story[(story.EXTRA_CATEGORY_REF == 0) & (story.CATEGORY_REF == 5)] # 0
story[(story.EXTRA_CATEGORY_REF == story.CATEGORY_REF) & (story.CATEGORY_REF == 0)]

story[(story.EXTRA_CATEGORY_REF == 0) & (story.CATEGORY_REF != 0)] #4086 rows
story[(story.EXTRA_CATEGORY_REF != 0) & (story.CATEGORY_REF == 0)] #6 rows
story[(story.EXTRA_CATEGORY_REF == 0) & (story.CATEGORY_REF == 0)] #0 row
story[(story.EXTRA_CATEGORY_REF != 0) & (story.CATEGORY_REF != 0)] #8523 rows
story
'''

# story.groupby('CATEGORY_REF').count()
# story.count()
# story['CATEGORY_REF'].count()

# select attributes needed from stories
stories = story[['STORY_ID', 'RATING', 'CATEGORY_REF',
                 'EXTRA_CATEGORY_REF', 'STORY_LANGUAGE',
                 'IS_PUBLISHED', 'IS_FINISHED', 'IS_CLASSIC']]


### STORY chapter distribution
chapter_story = story[['STORY_ID', 'NR_OF_CHAPTERS']]
print(100 * chapter_story[chapter_story['NR_OF_CHAPTERS'] < 3].count()['NR_OF_CHAPTERS']/chapter_story.count()['NR_OF_CHAPTERS'])
# 95.22% has 10 or fewer chapters
# 9

chapter_story = chapter_story[chapter_story['NR_OF_CHAPTERS'] < 11]

plt.hist(chapter_story['NR_OF_CHAPTERS'], log = True)
max(chapter_story['NR_OF_CHAPTERS'])
# MAX 902
# plt.hist(reads_hist['COUNT'], bins=10)
plt.ylabel('Number of Stories (log-scale)')
plt.xlabel('Number of Chapters of the Story')
plt.title('Histogram Number of Chapters per Story')
plt.show()



# -----------------------------------------------------------------------
# -----------------------------------------------------------------------
# -----------------------------------------------------------------------
## reads data

# number of chapters read per user
reads_per_person = reads.groupby('ACCOUNT_ID').sum()
reads_per_person.reset_index(level=0, inplace = True)
reads_per_person = reads_per_person[['ACCOUNT_ID', 'COUNT']]
reads_per_person.rename(columns={'COUNT': 'N_Chapters_Read'}, inplace=True)
print(reads_per_person)

# plot histogram

# reads_per_person.sort_values(by='N_Chapters_Read', ascending=False)
print(100 * reads_per_person[reads_per_person['N_Chapters_Read'] < 6].count()['N_Chapters_Read']/reads_per_person.count()['N_Chapters_Read'])

reads_per_person = reads_per_person[reads_per_person['N_Chapters_Read'] < 21]


plt.hist(reads_per_person['N_Chapters_Read'], log = True)
max(reads_per_person['N_Chapters_Read'])
# plt.hist(reads_hist['COUNT'], bins=10)
plt.ylabel('Number of Readers (log-scale)')
plt.xlabel('Cumulative Number of Chapters Read')
plt.title('Histogram Number of Chapters Read')
plt.show()

# -----------------------------------------------------------------------
# -----------------------------------------------------------------------
## date
## count of reads day of week
reads_by_day = reads.groupby('day_of_week').count()
# convert index to column
reads_by_day.reset_index(level=0, inplace = True)
print(reads_by_day)

# plot
x = reads_by_day['day_of_week']
y = reads_by_day['READ_ID']
plt.bar(x, y)
my_xticks = ['Monday','Tuesday','Wendnesday','Thursday', 'Friday', 'Saturday', 'Sunday']
plt.xticks(x, my_xticks)
plt.ylabel('Number of Reads')
plt.xlabel('Day of Week')
plt.xticks(rotation=-90)
plt.title('Reading Frequency Day of Week')
plt.show()

## sum of chapters day of week
# reads_story = reads[reads['COUNT'] > 0]
# reads_story = reads_story[reads_story['COUNT'] < 101]
reads_story_day = reads.groupby('day_of_week').sum()
reads_story_day.reset_index(level=0, inplace = True)
print(reads_story_day)

x = reads_story_day['day_of_week']
y = reads_story_day['COUNT']
plt.bar(x, y)
my_xticks = ['Monday','Tuesday','Wendnesday','Thursday', 'Friday', 'Saturday', 'Sunday']
plt.xticks(x, my_xticks)
plt.xticks(rotation=-90)
plt.ylabel('Number of Chapters Read')
plt.xlabel('Day of Week')
plt.title('Number of Chapters Read per Day of Week')
plt.show()

'''
## number of chapters per day of week
# reads_story = reads[reads['COUNT'] > 0]
# reads_story = reads_story[reads_story['COUNT'] < 101]
reads_story_day = reads.groupby('day_of_week').mean()
reads_story_day.reset_index(level=0, inplace = True)
print(reads_story_day)

x = reads_story_day['day_of_week']
y = reads_story_day['COUNT']
plt.bar(x, y)
plt.ylabel('Number of Chapters Read')
plt.xlabel('Day of Week')
plt.title('Average Number of Chapters Read per Day of Week')
plt.show()
'''

# -----------------------------------------------------------------------
# -----------------------------------------------------------------------

## time
# converting time to hour
def hr_func(ts):
    return ts.hour
reads['hour'] = reads['SERVER_EVENT_TIME'].apply(hr_func)
print(reads['hour'])

# count of reads; histogram
plt.hist(reads['hour'], bins=24)
plt.ylabel('Number of Reads')
plt.xlabel('Hours')
plt.title('Reading Frequency Daily Hour')
plt.show()


# sum of chapters per hour
reads_chapter_hour = reads.groupby('hour').sum()
reads_chapter_hour.reset_index(level=0, inplace = True)
print(reads_chapter_hour)

x = reads_chapter_hour['hour']
y = reads_chapter_hour['COUNT']
plt.bar(x, y)
plt.ylabel('Number of Chapters Read')
plt.xlabel('Hour')
plt.title('Number of Chapters Read per Hour')
plt.show()

# sum(reads_story['STORY_ID'])
# sum of stories per hour
# reads_story = reads.groupby(['STORY_ID', 'hour']).count()
# reads_story.reset_index(level=[0, 1], inplace = True)
reads_story = reads.groupby(by='hour', as_index=False).agg({'STORY_ID': pd.Series.nunique})
# alternative:
# reads.groupby('hour').STORY_ID.nunique()

print(reads_story)
x = reads_story['hour']
y = reads_story['STORY_ID']
plt.bar(x, y)
plt.ylabel('Number of Stories Read')
plt.xlabel('Hour')
plt.title('Number of Stories Read per Hour')
plt.show()

## per rating
# number of readings per rating
# set key
stories_to_merge = stories.copy()
reads_to_merge = reads.copy()
reads_to_merge.set_index('STORY_ID', inplace = True)
stories_to_merge.set_index('STORY_ID', inplace = True)
print(stories_to_merge)
print(reads_to_merge)




# merge
reads_stories = pd.concat([reads_to_merge, stories_to_merge],
                          axis=1, join_axes=[reads_to_merge.index])
# reads_stories.count()
reads_stories.reset_index(level=0, inplace = True)

# plot number of readings per rating
rating = reads_stories.groupby('RATING').count()
rating.reset_index(level=0, inplace = True)
print(rating)

x = rating['RATING']
y = rating['STORY_ID']
plt.bar(x, y)
plt.ylabel('Number of Reads')
plt.xlabel('RATING')
plt.title('Reading Frequency Each Rating')
plt.show()

# number of stories per rating
rating_story = stories.groupby('RATING').count()
rating_story.reset_index(level=0, inplace = True)
print(rating_story)

x = rating_story['RATING']
y = rating_story['STORY_ID']
plt.bar(x, y)
plt.ylabel('Number of Stories')
plt.xlabel('RATING')
plt.title('Number of Stories Each Rating')
plt.show()


# number of readers per rating

# plot number of readings per rating

readers_per_rating = reads_stories.groupby(by='RATING', as_index=False).agg({'ACCOUNT_ID': pd.Series.nunique})

print(readers_per_rating)

x = readers_per_rating['RATING']
y = readers_per_rating['ACCOUNT_ID']
plt.bar(x, y)
plt.ylabel('Number of Readers')
plt.xlabel('RATING')
plt.title('Number of Readers Each Rating')
plt.show()


# -----------------------------------------------------------------------
# -----------------------------------------------------------------------
## Genre
genres = pd.read_csv('genres.csv')

# combine genre and stories
stories.rename(columns={'CATEGORY_REF': 'category_ref'}, inplace=True)

# set index
stories.set_index('category_ref', inplace=True)
genres.set_index('category_ref', inplace = True)
# merge
genres_stories = pd.concat([stories, genres], axis=1, join_axes=[stories.index])
stories.reset_index(level=0, inplace = True)


genres_stories['name'] = genres_stories['name'].fillna('not specified')
genres_stories.reset_index(level=0, inplace = True)
print(genres_stories)

# extra category reference
genres.reset_index(level=0, inplace = True)
genres.rename(columns={'category_ref': 'EXTRA_CATEGORY_REF'}, inplace=True)
genres.rename(columns={'name': 'extra_name'}, inplace=True)

# set index
genres.set_index('EXTRA_CATEGORY_REF', inplace = True)
genres_stories.set_index('EXTRA_CATEGORY_REF', inplace = True)


# merge
genres_stories = pd.concat([genres_stories, genres], axis=1, join_axes=[genres_stories.index])

genres_stories['extra_name'] = genres_stories['extra_name'].fillna('not specified')
genres_stories.reset_index(level=0, inplace = True)

print(genres_stories.count())
print(genres_stories.dtypes)

## number of stories per genres
# see if there are redundant genres
# print(story[story['IS_PUBLISHED'] == 0].count()['IS_PUBLISHED'])

genres1 = genres_stories.groupby('name')[['name','STORY_ID']].count()
genres2 = genres_stories.groupby('extra_name')[['extra_name', 'STORY_ID']].count()
genres1.rename(columns={'name': 'extra_name'}, inplace=True)
print(genres1)
print(genres2)

genres_all = pd.concat([genres1, genres2]).groupby(level=0).sum()

genres_all.reset_index(level=0, inplace = True)
genres_all.rename(columns={'index': 'genres'}, inplace=True)
genres_all = genres_all[genres_all['genres'] != 'not specified']
print(genres_all)

x = genres_all['genres']
y = genres_all['STORY_ID']
plt.bar(x, y)
plt.ylabel('Number of Stories')
plt.xlabel('Genre Reference')
plt.title('Number of Stories per Genre')
plt.xticks(rotation=-90)
plt.show()



'''
# second genre
sec_genres_dis = genres_stories.groupby('EXTRA_CATEGORY_REF').count()
sec_genres_dis.reset_index(level=0, inplace = True)
# genres_dis = genres_dis.sort_values(by='extra_name', ascending=False)
print(sec_genres_dis)

x = sec_genres_dis['EXTRA_CATEGORY_REF']
y = sec_genres_dis['name']
plt.bar(x, y)
plt.ylabel('Number of Stories')
plt.xlabel('Genre First Category')
plt.title('Number of Stories per Genre')
plt.show()
'''

# ------------------------------------------------------------------------
# -----------------------------------------------------------------------
# number of genre reads histogram distribution
## merging reads and genres, getting specific genres for each reads
# combine genres_stories with Account ID
reads.set_index('STORY_ID', inplace = True)
genres_stories.set_index('STORY_ID', inplace = True)
reads_genres = pd.concat([reads, genres_stories], axis=1, join_axes=[reads.index])
# check NaN
# reads_genres[reads_genres.extra_name.isnull()]
genres_stories.reset_index(level=0, inplace = True)
reads_genres.reset_index(level=0, inplace = True)
reads.reset_index(level=0, inplace = True)

print(reads_genres.dtypes)

## get dummy variables for genres
genres1_per_reader = pd.get_dummies(reads_genres['name'])
genres2_per_reader = pd.get_dummies(reads_genres['extra_name'])

genres_per_reader = pd.concat([genres1_per_reader, genres2_per_reader]).groupby(level=0).sum()
print(genres_per_reader)

## combine to reads
# sum(genres_per_reader[5] > 2)
reads_with_genres = pd.concat([reads_genres, genres_per_reader], axis=1)

# delete category 0
reads_with_genres = reads_with_genres.drop('not specified', 1)
reads_with_genres.dtypes

# group by users
genres_readers = reads_with_genres.groupby('ACCOUNT_ID').sum()
genres_readers.reset_index(level=0, inplace = True)

genres_readers['n_genres_read'] = (genres_readers.iloc[:, 10:] > 0).astype(int).sum(axis=1)
# print(genres_readers.iloc[1, 9:])
genres_readers = genres_readers[genres_readers['n_genres_read'] != 0]

print(100 * genres_readers[genres_readers['n_genres_read'] < 2].count()['n_genres_read']/genres_readers.count()['n_genres_read'])
# 99% of the readers read 15 genres or fewer;
# 97.6% read fewer than 11 genres; 89% read fewer than 6; 10% read only 1 genre
# 53.6% read 2 genres; 9.9% read 3; 84.2% read 4 or fewer genres

genres_readers = genres_readers[genres_readers['n_genres_read'] < 11]



plt.hist(genres_readers['n_genres_read'], bins = 10)
plt.ylabel('Number of Readers')
plt.xlabel('Number of Genres Read')
plt.title('Histogram Number of Genres Read')
plt.show()

print(genres_readers.dtypes)
# set as integer
genres_readers.iloc[:, 10:] = genres_readers.iloc[:, 10:].astype(int)

# ------------------------------------------------------------------------
# ------------------------------------------------------------------------
# number of reads each genre

reads_genres1 = reads_genres.groupby('name')[['name','ACCOUNT_ID']].count()
reads_genres2 = reads_genres.groupby('extra_name')[['extra_name','ACCOUNT_ID']].count()
reads_genres1.rename(columns={'name': 'extra_name'}, inplace=True)
print(reads_genres1)
print(reads_genres2)

reads_genres_all = pd.concat([reads_genres1, reads_genres2]).groupby(level=0).sum()

reads_genres_all.reset_index(level=0, inplace = True)
reads_genres_all.rename(columns={'index': 'genres'}, inplace=True)
reads_genres_all = reads_genres_all [reads_genres_all ['genres'] != 'not specified']
print(reads_genres_all)
print(genres_all)

x = reads_genres_all['genres']
y = reads_genres_all['ACCOUNT_ID']
plt.bar(x, y)
plt.ylabel('Number of Reads')
plt.xlabel('Genres')
plt.title('Number of Reads per Genre')
plt.xticks(rotation=-90)
plt.show()


reads_per_story_per_genre = reads_genres_all.merge(genres_all, on='genres')

reads_per_story_per_genre['reads_per_story_per_genre'] = reads_per_story_per_genre['extra_name_x']/reads_per_story_per_genre['extra_name_y']
x = reads_per_story_per_genre['genres']
y = reads_per_story_per_genre['reads_per_story_per_genre']
plt.bar(x, y)
plt.ylabel('Number of Reads per Story')
plt.xlabel('Genres')
plt.title('Number of Reads per Story Each Genre')
plt.xticks(rotation=-90)
plt.show()

print(reads_per_story_per_genre)


'''
# ------------------------------------------------------------------------
# ------------------------------------------------------------------------
### clustering

# genres_readers.loc[(genres_readers.10 == 0) & (story.CATEGORY_REF == 5),
#                     'EXTRA_CATEGORY_REF'] = 1

# make all dummy equals 0 or 1
Result = ['action and adventure', 'arts and culture', 'biographies',
           'blogging', 'business and management', 'chicklit',
           'classics', 'education and teaching', 'fanfiction',
           'fantasy', 'general fiction', 'general non-fiction',
           'health and food', 'historical fiction', 'humour',
           'journalism', 'mystery and crime', 'other', 'pets and animals',
           'poetry', 'religion and spirituality', 'romance',
           'science fiction', 'sciences', 'sports', 'teen and young adult',
           'thriller and horror', 'travel']
genres_readers_new = pd.DataFrame(np.where(genres_readers[Result] == 0, 0, 1),
                  index = genres_readers.index, columns= Result)

print(genres_readers_new)
# combine
user_reading = reads_genres.groupby('ACCOUNT_ID').count()
user_reading.reset_index(level=0, inplace = True)
readers_genres = pd.concat([user_reading, genres_readers_new], axis=1)
print(readers_genres)

readers_genres.to_csv('readers_genres.csv')
readers_genres.to_csv('example.txt', sep='\t')



# summing up number of genres per read
reads_with_genres.iloc[:, 5:] = reads_with_genres.iloc[:, 5:].astype(float)
reads_with_genres['n_genres'] = reads_with_genres.iloc[:, 5:].sum(1)
print(reads_with_genres)

reads_with_genres.iloc[:, 5:] = reads_with_genres.iloc[:, 5:].astype(int)
sum(reads_with_genres['n_genres'] == 2)


df = pd.DataFrame(np.random.randn(4,3),columns = ['col1','col2','col3'])
print(df)

for index, row in df.iterrows():
   row['a'] = 10
print df
'''
