FROM python:3
# install libpcap
RUN apt-get update
RUN apt-get install -y libpcap-dev

WORKDIR /app
# install python requirements
COPY requirements.txt /app/requirements.txt
RUN pip install -r requirements.txt
# copy the app
COPY . /app
# set the entrypoint
ENTRYPOINT ["python3", "sniffer.py"]
