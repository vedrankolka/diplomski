import time
from locust import HttpUser, task, between

class NormalUser(HttpUser):

    wait_time = between(1, 5)

    @task
    def get_index(self):
        self.client.get("/")

    @task(3)
    def get_post1(self):
        self.client.get("/?p=21")

    @task
    def get_about(self):
        self.client.get("/?page_id=2")

    @task
    def download_hall_of_fame(self):
        self.client.get("/wp-content/uploads/2022/03/hallOfFame.txt")

    @task(2)
    def download_history(self):
        self.client.get("/wp-content/uploads/2022/03/history.csv")
    