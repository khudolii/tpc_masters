# tpc_masters
Classes:

TurboCodeDecoder.class - реалізує алгоритм декодування турбокоду

TurboCodeDecoderVO.class - клас включає поля потрібні для декодування для кожного рядка 

DecodeUtil.class - клас який включає методи спільні для різних видів декодування. Також включає константи

TransportDelegate.class - виконує функцію прийняття вхідної матриці, запуску процесу декодування, повертання декодованого повідомлення

TurboCodeServlet.class - отримує повідомлення з вебу, по actionName виконує певну дію

DecoderBean.class - класс з полями які використовуються для вебу

DecoderExceotion, ReportException - виключення 
