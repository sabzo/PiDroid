import subprocess
class Nobu:
    def __init__(self, ssid, password):
        self.ssid = ssid
        self.password = password
        self.create_hostapd_conf()
        self.create_dhcpd_conf()
        self.create_interfaces_conf()
        # write conf files to disk. Location is current directory '.'
        conf_files = [('hostapd.conf.ap', self.hostapd_conf), ('dhcpd.conf.ap', self.dhcpd_conf), ('interfaces.ap', self.interfaces_conf)]
        for conf in conf_files:
          self.create_conf_file(conf[0], conf[1]) # 0 = filename, 1 = configuration text

    def create_conf_file(self, filename, text):
        f = open(filename, 'w')
        f.write(text)

    def create_hostapd_conf(self):
        self.hostapd_conf = """# Basic configuration
interface=wlan0
ssid=%s
channel=1

# WPA and WPA2 configuration
macaddr_acl=0
auth_algs=1
ignore_broadcast_ssid=0
wpa=3
wpa_passphrase=%s
wpa_key_mgmt=WPA-PSK
wpa_pairwise=TKIP
rsn_pairwise=CCMP

# Hardware configuration
driver=rtl871xdrv
ieee80211n=1
hw_mode=g
device_name=RTL8192CU
manufacturer=Realtek""" % (self.ssid, self.password)

    def create_dhcpd_conf(self):
        self.dhcpd_conf = """# The ddns-updates-style parameter controls whether or not the server will
# attempt to do a DNS update when a lease is confirmed. We default to the
# behavior of the version 2 packages ('none', since DHCP v2 didn't
# have support for DDNS.)
ddns-update-style none;

default-lease-time 600;
max-lease-time 7200;

# If this DHCP server is the official DHCP server for the local
# network, the authoritative directive should be uncommented.
authoritative;

# Use this to send dhcp log messages to a different log file (you also
# have to hack syslog.conf to complete the redirection).
log-facility local7;

subnet 192.168.42.0 netmask 255.255.255.0 {
  range 192.168.42.10 192.168.42.50;
  option broadcast-address 192.168.42.255;
  option routers 192.168.42.1;
  default-lease-time 600;
  max-lease-time 7200;
  option domain-name "local";
  option domain-name-servers 8.8.8.8, 8.8.4.4;
}
"""

    def create_interfaces_conf(self):
        self.interfaces_conf = """auto lo

iface lo inet loopback
iface eth0 inet dhcp

allow-hotplug wlan0

iface wlan0 inet static
  address 192.168.42.1
  netmask 255.255.255.0
"""
    def stdout(self, args):
        cmd = subprocess.Popen(args, shell=True, stdout=subprocess.PIPE)
        (stdoutdata, stderrdata) = cmd.communicate()
        return stdoutdata
