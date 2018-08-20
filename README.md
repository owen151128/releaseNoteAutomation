# JIRA Release Note Automation Tool
<a href="https://github.com/owen151128/releaseNoteAutomation/releases"><img src="https://img.shields.io/badge/Latest%20release-1.1.0-brightgreen.svg"/></a>

**Hyperconnect Quality Assurance** Automation Tool Project.

## Required
### Installation
> Java Runtime Environment(Java Virtual Machine)

### OS Support
>- **Microsoft Windows**
>- **Apple OS X**
>- **Linux**
>- **Unix**
>- **Oracle Solaris**

## Usage
1. Sign in to JIRA
>1. click `로그인` Button
>2. input `JIRA account`
>3. click `로그인` Button or click `ENTER`
2. input `JIRA Release Note URL`
3. click `Crate ReleaseNote` button click
4. wait a minutes
5. it's done, `GoogleSpreadsheet` created

## Description
Create Release Note `GoogleSpreadsheet` when Input `JIRA Release Note URL`.

Support `session cache`, Cache storage is `$HOME/.credentials/`

## Coding Language
*Java* 1.8

## Used Technology
### technologies
>- Google API
>- Web Crawling
>- Json
>- Gson
### Libraries
>- Google-API-Client 1.23.0
>- Google-OAuth-Client-jetty 1.23.0
>- Google-API-services-sheets v3-rev110-1.23.0
>- Google-API-services-drive v3-rev110-1.23.0
>- Google-code-Gson 2.8.2
>- BeautifulSoup-Jsoup 1.11.2
