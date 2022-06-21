from locust import HttpUser, task, between

class NormalUser(HttpUser):
    '''the time a user waits between executing tasks'''
    wait_time = between(3, 10)
    
    @task(5)
    def get_index(self):
        '''Get index and cover image.'''
        self.client.get("/")
        self.client.get("/wp-content/uploads/2022/06/cover.jpeg")

    @task(3)
    def get_post1(self):
        '''Get first post and featured image'''
        self.client.get("/?p=9")
        self.client.get("/wp-content/uploads/2022/06/organizatori.jpeg")

    @task(3)
    def get_post2(self):
        '''Get second post and featured image.'''
        self.client.get("/?p=24")

    @task(3)
    def get_about(self):
        '''Get the "about" page and featured image.'''
        self.client.get("/?page_id=2")
        self.client.get("/wp-content/uploads/2022/06/ekipa.jpeg")

    @task(2)
    def get_HallOfFame(self):
        self.client.get("/wp-content/uploads/2022/06/HallOfFame.txt")

    @task(2)
    def get_history(self):
        self.client.get("/wp-content/uploads/2022/06/history.csv")

    @task(1)
    def get_archive(self):
        self.client.get("/wp-content/uploads/2022/06/archive.zip")
