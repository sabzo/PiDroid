import subprocess
import textwrap
class Nobu:
    def __init__(self, ssid, password):
        self.ssid = ssid
        self.password = password
        # Get the configuration settings (text)
        hostapd_conf = self.create_hostapd_conf()
        interfaces_conf = self.create_interfaces_conf()
        dhcpd_conf = self.create_dhcpd_conf()
        # create the necessary linux commands to create an AccessPoint
        hostapd_cmd = 'sudo hostapd hostapd.conf.ap &' # run as daemon
        interface_cmd = ['sudo ifdown wlan0','sudo ifconfig wlan0 192.168.42.1', 'sudo ifup wlan0']
        dhcpd_cmd = 'sudo dhcpd -cf dhcpd.conf.ap'
        conf_files = [('hostapd.conf.ap', hostapd_conf, hostapd_cmd), \
            ('interfaces.ap', interfaces_conf, interface_cmd ), \
            ('dhcpd.conf.ap', dhcpd_conf, dhcpd_cmd)]
        # write conf files to disk. Location is current directory '.'
        for conf in conf_files:
          self.create_conf_file(conf[0], conf[1]) # 0 = filename, 1 = configuration text
          self.run_command(conf[2]) # 2 is the command in the tuple `conf`

    # Write the configuration file to disk
    def create_conf_file(self, filename, text):
        f = open(filename, 'w')
        f.write(text)
    # Returns the hostapd configuration text
    def create_hostapd_conf(self):
        hostapd_conf = textwrap.dedent("""\
            # Basic configuration
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
            manufacturer=Realtek""") % (self.ssid, self.password)
        return hostapd_conf
    # returns the dhcpd configuration text
    def create_dhcpd_conf(self):
        dhcpd_conf = textwrap.dedent("""\
            # The ddns-updates-style parameter controls whether or not the server will
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
            """)
        return dhcpd_conf
    # returns the interfaces configuration text
    def create_interfaces_conf(self):
        interfaces_conf = textwrap.dedent("""\
            auto lo
            iface lo inet loopback
            iface eth0 inet dhcp
            allow-hotplug wlan0
            iface wlan0 inet static
              address 192.168.42.1
              netmask 255.255.255.0
            """)
        return interfaces_conf
    # helper function to write linux commands
    def run_command(self, args):
        print(args)
        cmd = subprocess.Popen(args, shell=isinstance(args, basestring), stdout=subprocess.PIPE)
        (stdoutdata, stderrdata) = cmd.communicate()
        return stdoutdata
