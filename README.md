# Masters Thesis
This is the repository for the project developed for my masters thesis.
In this brach, only the files needed for running the project is given, all of the source code can be found on branch main.

## Prerequisites
- Linux
- Docker
- Docker Compose

## Instalation

```bash
git clone --single-branch --branch deploy git@github.com:vedrankolka/diplomski.git
cd diplomski
export DIPLOMSKI_HOME=$PWD
chmod +x scripts/*
```

## Starting & stopping
For running the next scripts, if `DIPLOMSKI_HOME` is not set, position yourself in the root of the project.

To configure the tool, edit the `.env` file. All of the variables are described in the file.
### Start
> `./scripts/start.sh`

### Stop
> `./scripts/stop.sh`
