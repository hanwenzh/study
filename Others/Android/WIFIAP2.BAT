echo off

mode con cols=52 lines=22

title win7����·�� By̤ѩ�޺�

color 5f

cls

:wifi

echo.

echo     �q--------------------------------------�r

echo    ��         win7����WiFi�Զ�����           ��

echo    ��----------------------------------------��

echo    ��     �Զ�WiFi����                       �U

echo    ��   �ֻ������Թ�������                   �U

echo    �� ��رշ���ǽ������ɱ������������ʹ��   �U

echo    ��                            By HL       �U

echo    ��                                        �U

echo    ��                         2013.5         �U

echo    �t=======================================-�s

echo.



:AUTO  

echo ������Ҫ����

sc config   MpsSvc start= AUTO

sc start MpsSvc

sc config   LanmanServer  start= AUTO

sc start LanmanServer

sc config   LanmanWorkstation start= AUTO

sc start LanmanWorkstation

sc config   Browser start= AUTO

sc start Browser

sc config   Dhcp start= AUTO

sc start Dhcp

sc config   RpcSs start= AUTO

sc start RpcSs

sc config   RpcLocator start= AUTO

sc start RpcLocator

sc config   Dnscache start= AUTO

sc start Dnscache

sc config   FDResPub start= AUTO

sc start FDResPub

sc config   upnphost start= AUTO

sc start upnphost

sc config   SSDPSRV start= AUTO

sc start   SSDPSRV

sc config   lmhosts start= AUTO

sc start lmhosts

echo.����wifi�˺�

netsh wlan set hostednetwork mode=allow

netsh wlan set hostednetwork ssid=WIN7WIFI2 

echo. ��������

netsh wlan set hostednetwork key=1234567800

netsh wlan start hostednetwork

echo.

cls

:d

color 2f

cls

title �鿴WIFI����״̬

echo.

netsh wlan show hostednetwork
netsh wlan show drivers

echo.

pause

echo ���ʧ�ܣ���������������Microsoft Virtual Wifi Miniport...
pause