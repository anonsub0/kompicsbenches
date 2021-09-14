Omni-Paxos Experiments
===========================================
# Experimental Setup/Requirements
- Ubuntu 18.04
- Rust: rustc 1.53.0-nightly (42816d61e 2021-04-24)
- JDK: openjdk 11.0.11 2021-04-20
- SBT
- Ammonite
- protobuf 3.6 (in particular protoc)

(See next section for installing all of these)

 We used Google Cloud Compute `e2-standard-8` instances. We require one client instance and between 3-8 server instances. In Running we will provide the number of instances required per experiment.

 Installation Guide and Building must be performed on all VM instances. For convenience, it is recommended to do all steps for the client instancce first and then create a machine image from it that is used to deploy the server instances. Furthermore, we recommend using the internal IP addresses when needed in the following steps.

# Installation Guide

1. Install `zip` and `unzip` with `apt-get install <zip | unzip>`
2. Clone this repo and run installation script: `./install_dependencies.sh`
3. Install specific Rust version and set it to default: `rustup toolchain install nightly-2021-04-25` and `rustup default nightly-2021-04-25`

On client instance:

4. Create ssh keys: `ssh-keygen`

On all server instances:

5. Add the public key created in step 4 to `~/.ssh/authorized_keys`

On client instance:

6. `ssh` to all servers to authenticate and make sure client can reach servers.
7. Add all client ip addresses to the file `nodes.conf`. Also add all ip addresses to the file `kompact/configs/atomic_broadcast.conf` under "deployment"
8. Set `runnerAddr`  to `<client_ip>:45678` and `masterAddr` to `<client_ip>:45679` [here](https://github.com/anonsub0/kompicsbenches/blob/main/bench.sc#L18-L20). Furthermore set ssh key [here](https://github.com/anonsub0/kompicsbenches/blob/main/bench.sc#L326).

# Building
On all instances:

`./build.sc --useOnly shared_scala; ./build.sc --useOnly runner; ./build.sc --useOnly kompact`

# Running
On client instance (after built on all instances):
`./bench.sc remote --impls KOMPACTMIX --benchmarks ATOMICBROADCAST --runName <name of experiment>`

For each experiment, corresponding branch (and `--runName` to use) and number of required instances are as follows:

(**Make sure to switch branch on all server instances as well**! However, when changing to a `-raftpv` branch, then only need to recompile at client.)

## General Performance
* Instances required: 1 (client) + 5 (servers)
* Branch: `general`

## Full Partition
* Instances required: 1 (client) + 3 (servers)
* Branch: `periodic` and `periodic-raftpv` 

## Chained Partition
* Instances required: 1 (client) + 3 (servers)
* Branch: `chained` and `chained-raftpv` 

## Deadlock Partition
* Instances required: 1 (client) + 5 (servers)
* Branch: `deadlock` and `deadlock-raftpv` 

## Reconfiguration
* Instances required: 1 (client) + 8 (servers)
* Branch: `reconfig`

## Leader Election
* Instances required: 1 (client) + 3 (servers)
* Branch: `leader`

Make sure to update `nodes.conf` with new ip addresses and put the straggler server i.e. with weaker cpu or worse location as `1` in `kompact/configs/atomic_broadcast.conf`

# Collecting Results
We will know collect all remote logs and results into one zip file. For each experiment i.e. branch:
1. ssh into all server instances and run: `./zip_logs_results.sh <branch_name>`

On client instance:

2. `mkdir meta_results/<branch_name>/remote `
3. run for each server instance: `scp <instance_name>@<instance_ip>:~/kompicsbenches/zipped/<branch_name>.zip`
4. `./zip_logs_results.sh`

All results, meta results and logs will be at: `meta_results/<branch_name>.zip`

# Plotting
See [Plotting.md](https://github.com/anonsub0/kompicsbenches/blob/main/Plotting.md).